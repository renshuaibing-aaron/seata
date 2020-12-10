package io.seata.spring.annotation.datasource;

import javax.sql.DataSource;
import java.lang.reflect.Method;

import io.seata.rm.datasource.DataSourceProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInfo;
import org.springframework.beans.BeanUtils;

/**
 * @author xingfudeshi@gmail.com
 */
public class SeataAutoDataSourceProxyAdvice implements MethodInterceptor, IntroductionInfo {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        DataSourceProxy dataSourceProxy = DataSourceProxyHolder.get().putDataSource((DataSource) invocation.getThis());
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();
        Method m = BeanUtils.findDeclaredMethod(DataSourceProxy.class, method.getName(), method.getParameterTypes());
        if (null != m) {
            return m.invoke(dataSourceProxy, args);
        } else {
            return invocation.proceed();
        }
    }

    @Override
    public Class<?>[] getInterfaces() {
        return new Class[]{SeataProxy.class};
    }

}
