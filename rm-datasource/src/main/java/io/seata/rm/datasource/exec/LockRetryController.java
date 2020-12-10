package io.seata.rm.datasource.exec;

import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;

import static io.seata.core.constants.DefaultValues.DEFAULT_CLIENT_LOCK_RETRY_INTERVAL;
import static io.seata.core.constants.DefaultValues.DEFAULT_CLIENT_LOCK_RETRY_TIMES;

/**
 * The type Lock retry controller.
 *
 * @author sharajava
 */
public class LockRetryController {
    private static int LOCK_RETRY_INTERNAL =
        ConfigurationFactory.getInstance().getInt(ConfigurationKeys.CLIENT_LOCK_RETRY_INTERVAL, DEFAULT_CLIENT_LOCK_RETRY_INTERVAL);
    private static int LOCK_RETRY_TIMES =
        ConfigurationFactory.getInstance().getInt(ConfigurationKeys.CLIENT_LOCK_RETRY_TIMES, DEFAULT_CLIENT_LOCK_RETRY_TIMES);

    private int lockRetryInternal = LOCK_RETRY_INTERNAL;
    private int lockRetryTimes = LOCK_RETRY_TIMES;

    /**
     * Instantiates a new Lock retry controller.
     */
    public LockRetryController() {
    }

    /**
     *
     * 遇到锁冲突 需要等一等再进行
     * Sleep.
     *
     * @param e the e
     * @throws LockWaitTimeoutException the lock wait timeout exception
     */
    public void sleep(Exception e) throws LockWaitTimeoutException {
        if (--lockRetryTimes < 0) {
            throw new LockWaitTimeoutException("Global lock wait timeout", e);
        }

        try {
            Thread.sleep(lockRetryInternal);
        } catch (InterruptedException ignore) {
        }
    }
}