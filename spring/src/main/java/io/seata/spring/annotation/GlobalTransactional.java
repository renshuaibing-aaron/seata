package io.seata.spring.annotation;

import io.seata.tm.api.transaction.Propagation;
import io.seata.tm.api.transaction.TransactionInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Global transactional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface GlobalTransactional {

    /**
     * Global transaction timeoutMills in MILLISECONDS.
     *
     * @return timeoutMills in MILLISECONDS.
     */
    int timeoutMills() default TransactionInfo.DEFAULT_TIME_OUT;

    /**
     * Given name of the global transaction instance.
     *
     * @return Given name.
     */
    String name() default "";

    /**
     * roll back for the Class
     * @return
     */
    Class<? extends Throwable>[] rollbackFor() default {};

    /**
     *  roll back for the class name
     * @return
     */
    String[] rollbackForClassName() default {};

    /**
     * not roll back for the Class
     * @return
     */
    Class<? extends Throwable>[] noRollbackFor() default {};

    /**
     * not roll back for the class name
     * @return
     */
    String[] noRollbackForClassName() default {};

    /**
     * the propagation of the global transaction
     * @return
     */
    Propagation propagation() default Propagation.REQUIRED;
}
