package io.seata.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import io.seata.common.exception.ShouldNeverHappenException;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationChangeEvent;
import io.seata.config.ConfigurationChangeListener;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.rm.GlobalLockTemplate;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;
import io.seata.tm.api.TransactionalExecutor;
import io.seata.tm.api.TransactionalTemplate;
import io.seata.tm.api.transaction.NoRollbackRule;
import io.seata.tm.api.transaction.RollbackRule;
import io.seata.tm.api.transaction.TransactionInfo;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import static io.seata.core.constants.DefaultValues.DEFAULT_DISABLE_GLOBAL_TRANSACTION;

/**
 * The type Global transactional interceptor.
 *   这个是对应的Advices/Advisors
 *   全局事务的拦截器
 * @author slievrly
 */
public class GlobalTransactionalInterceptor implements ConfigurationChangeListener, MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalTransactionalInterceptor.class);
    private static final FailureHandler DEFAULT_FAIL_HANDLER = new DefaultFailureHandlerImpl();

    private final TransactionalTemplate transactionalTemplate = new TransactionalTemplate();
    private final GlobalLockTemplate<Object> globalLockTemplate = new GlobalLockTemplate<>();
    private final FailureHandler failureHandler;
    private volatile boolean disable;

    /**
     * Instantiates a new Global transactional interceptor.
     *
     * @param failureHandler the failure handler
     */
    public GlobalTransactionalInterceptor(FailureHandler failureHandler) {
        this.failureHandler = failureHandler == null ? DEFAULT_FAIL_HANDLER : failureHandler;
        this.disable = ConfigurationFactory.getInstance().getBoolean(ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
            DEFAULT_DISABLE_GLOBAL_TRANSACTION);
    }

    /**
     * 如果方法上有全局事务注解，调用handleGlobalTransaction开启全局事务
     * 如果没有，按普通方法执行，避免性能下降
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        Class<?> targetClass = methodInvocation.getThis() != null ? AopUtils.getTargetClass(methodInvocation.getThis())
            : null;
        Method specificMethod = ClassUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
        final Method method = BridgeMethodResolver.findBridgedMethod(specificMethod);

        //获取方法GlobalTransactional注解
        final GlobalTransactional globalTransactionalAnnotation = getAnnotation(method, GlobalTransactional.class);
        final GlobalLock globalLockAnnotation = getAnnotation(method, GlobalLock.class);

        //如果方法有GlobalTransactional注解，则拦截到相应方法处理
        if (!disable && globalTransactionalAnnotation != null) {
            return handleGlobalTransaction(methodInvocation, globalTransactionalAnnotation);

        } else if (!disable && globalLockAnnotation != null) {
            System.out.println("======全局锁的方法======");
            return handleGlobalLock(methodInvocation);

        } else {
            return methodInvocation.proceed();
        }
    }

    private Object handleGlobalLock(final MethodInvocation methodInvocation) throws Exception {
        return globalLockTemplate.execute(() -> {
            try {
                return methodInvocation.proceed();
            } catch (Exception e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Object handleGlobalTransaction(final MethodInvocation methodInvocation,
                                           final GlobalTransactional globalTrxAnno) throws Throwable {
        try {

            //最终调用的是TransactionalTemplate的execute方法
            //这个模板方法定义了TM对全局事务处理的标准步骤
            return transactionalTemplate.execute(new TransactionalExecutor() {
                @Override
                public Object execute() throws Throwable {
                    return methodInvocation.proceed();
                }

                public String name() {
                    String name = globalTrxAnno.name();
                    if (!StringUtils.isNullOrEmpty(name)) {
                        return name;
                    }
                    return formatMethod(methodInvocation.getMethod());
                }

                @Override
                public TransactionInfo getTransactionInfo() {

                    TransactionInfo transactionInfo = new TransactionInfo();
                    transactionInfo.setTimeOut(globalTrxAnno.timeoutMills());
                    transactionInfo.setName(name());
                    transactionInfo.setPropagation(globalTrxAnno.propagation());  //事务传播类型

                    Set<RollbackRule> rollbackRules = new LinkedHashSet<>();

                    for (Class<?> rbRule : globalTrxAnno.rollbackFor()) {
                        rollbackRules.add(new RollbackRule(rbRule));
                    }
                    for (String rbRule : globalTrxAnno.rollbackForClassName()) {
                        rollbackRules.add(new RollbackRule(rbRule));
                    }
                    for (Class<?> rbRule : globalTrxAnno.noRollbackFor()) {
                        rollbackRules.add(new NoRollbackRule(rbRule));
                    }
                    for (String rbRule : globalTrxAnno.noRollbackForClassName()) {
                        rollbackRules.add(new NoRollbackRule(rbRule));
                    }
                    transactionInfo.setRollbackRules(rollbackRules);
                    return transactionInfo;
                }
            });
        } catch (TransactionalExecutor.ExecutionException e) {
            TransactionalExecutor.Code code = e.getCode();
            switch (code) {
                case RollbackDone:
                    throw e.getOriginalException();
                case BeginFailure:
                    failureHandler.onBeginFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case CommitFailure:
                    failureHandler.onCommitFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case RollbackFailure:
                    failureHandler.onRollbackFailure(e.getTransaction(), e.getCause());
                    throw e.getCause();
                case RollbackRetrying:
                    failureHandler.onRollbackRetrying(e.getTransaction(), e.getCause());
                    throw e.getCause();
                default:
                    throw new ShouldNeverHappenException(String.format("Unknown TransactionalExecutor.Code: %s", code));

            }
        }
    }

    private <T extends Annotation> T getAnnotation(Method method, Class<T> clazz) {
        return method == null ? null : method.getAnnotation(clazz);
    }

    private String formatMethod(Method method) {
        StringBuilder sb = new StringBuilder(method.getName()).append("(");

        Class<?>[] params = method.getParameterTypes();
        int in = 0;
        for (Class<?> clazz : params) {
            sb.append(clazz.getName());
            if (++in < params.length) {
                sb.append(", ");
            }
        }
        return sb.append(")").toString();
    }

    @Override
    public void onChangeEvent(ConfigurationChangeEvent event) {
        if (ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION.equals(event.getDataId())) {
            LOGGER.info("{} config changed, old value:{}, new value:{}", ConfigurationKeys.DISABLE_GLOBAL_TRANSACTION,
                disable, event.getNewValue());
            disable = Boolean.parseBoolean(event.getNewValue().trim());
        }
    }
}
