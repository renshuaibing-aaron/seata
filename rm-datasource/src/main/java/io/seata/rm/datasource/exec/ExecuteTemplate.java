package io.seata.rm.datasource.exec;

import io.seata.common.util.CollectionUtils;
import io.seata.core.context.RootContext;
import io.seata.rm.datasource.StatementProxy;
import io.seata.rm.datasource.sql.SQLVisitorFactory;
import io.seata.sqlparser.SQLRecognizer;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * The type Execute template.
 *
 * @author sharajava
 */
public class ExecuteTemplate {

    /**
     * Execute t.
     *
     * @param <T>               the type parameter
     * @param <S>               the type parameter
     * @param statementProxy    the statement proxy
     * @param statementCallback the statement callback
     * @param args              the args
     * @return the t
     * @throws SQLException the sql exception
     */
    public static <T, S extends Statement> T execute(StatementProxy<S> statementProxy,
                                                     StatementCallback<T, S> statementCallback,
                                                     Object... args) throws SQLException {
        return execute(null, statementProxy, statementCallback, args);
    }

    /**
     * Execute t.
     *
     * @param <T>               the type parameter
     * @param <S>               the type parameter
     * @param sqlRecognizers     the sql recognizer
     * @param statementProxy    the statement proxy
     * @param statementCallback the statement callback
     * @param args              the args
     * @return the t
     * @throws SQLException the sql exception
     */
    public static <T, S extends Statement> T execute(List<SQLRecognizer> sqlRecognizers,
                                                     StatementProxy<S> statementProxy,
                                                     StatementCallback<T, S> statementCallback,
                                                     Object... args) throws SQLException {

        System.out.println("通过层层代理准备执行sql语句");

        //首先检查当前事务是不是处于全局事务中
        if (!RootContext.inGlobalTransaction() && !RootContext.requireGlobalLock()) {
            // Just work as original statement
            // 没有分布式事务，执行像普通sql一样执行
            return statementCallback.execute(statementProxy.getTargetStatement(), args);
        }

        //生成一个sql的识别器对象，它能解析sql
        //根据sql语句和数据库类型获取SQL识别器
        if (sqlRecognizers == null) {
            sqlRecognizers = SQLVisitorFactory.get(
                    statementProxy.getTargetSQL(),
                    statementProxy.getConnectionProxy().getDbType());
        }

        //sql 执行器 根据不同的sql生成不同的执行器
        Executor<T> executor;

        //根据sql的类型生成不同的类型的executor，传入不同发statementCallback
        if (CollectionUtils.isEmpty(sqlRecognizers)) {
            executor = new PlainExecutor<>(statementProxy, statementCallback);
        } else {
            if (sqlRecognizers.size() == 1) {
                SQLRecognizer sqlRecognizer = sqlRecognizers.get(0);
                switch (sqlRecognizer.getSQLType()) {
                    case INSERT:
                        executor = new InsertExecutor<>(statementProxy, statementCallback, sqlRecognizer);
                        break;
                    case UPDATE:
                        executor = new UpdateExecutor<>(statementProxy, statementCallback, sqlRecognizer);
                        break;
                    case DELETE:
                        executor = new DeleteExecutor<>(statementProxy, statementCallback, sqlRecognizer);
                        break;
                    case SELECT_FOR_UPDATE:
                        executor = new SelectForUpdateExecutor<>(statementProxy, statementCallback, sqlRecognizer);
                        break;
                    default:
                        executor = new PlainExecutor<>(statementProxy, statementCallback);
                        break;
                }
            } else {
                executor = new MultiExecutor<>(statementProxy, statementCallback, sqlRecognizers);
            }
        }
        T rs;
        try {
            //执行sql
            rs = executor.execute(args);
        } catch (Throwable ex) {
            if (!(ex instanceof SQLException)) {
                // Turn other exception into SQLException
                ex = new SQLException(ex);
            }
            throw (SQLException) ex;
        }
        return rs;
    }
}
