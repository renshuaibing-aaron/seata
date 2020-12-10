package io.seata.rm.datasource.exec;

/**
 * The interface Executor.
 *
 * @author sharajava
 *
 * @param <T> the type parameter
 */
public interface Executor<T> {

    /**
     * Execute t.
     *
     * @param args the args
     * @return the t
     * @throws Throwable the throwable
     */
    T execute(Object... args) throws Throwable;
}
