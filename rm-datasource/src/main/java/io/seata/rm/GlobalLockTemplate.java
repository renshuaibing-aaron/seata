package io.seata.rm;

import java.util.concurrent.Callable;

import io.seata.core.context.RootContext;

/**
 * Template of executing business logic in a local transaction with Global lock.
 *
 * @param <T>
 * @author deyou
 */
public class GlobalLockTemplate<T> {

    /**
     * Execute object.
     *
     * @param business the business
     * @return the object
     * @throws Exception
     */
    public Object execute(Callable<T> business) throws Exception {

        Object rs;
        try {
            System.out.println("======全局锁的方法=1=====");
            // add global lock declare
            RootContext.bindGlobalLockFlag();
            System.out.println("======全局锁的方法==2====");
            // Do Your Business
            rs = business.call();


        } finally {
            System.out.println("======全局锁的方法==3====");
            //clean the global lock declare
            RootContext.unbindGlobalLockFlag();
        }

        return rs;
    }

}
