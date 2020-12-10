package io.seata.common;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * The type Application keeper.
 *
 * @author sharajava
 */
public class ApplicationKeeper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationKeeper.class);

    private final ReentrantLock LOCK = new ReentrantLock();
    private final Condition STOP = LOCK.newCondition();

    /**
     * Instantiates a new Application keeper.
     *
     * @param applicationContext the application context
     */
    public ApplicationKeeper(AbstractApplicationContext applicationContext) {
        addShutdownHook(applicationContext);
    }

    private void addShutdownHook(final AbstractApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    applicationContext.close();
                    LOGGER.info("ApplicationContext " + applicationContext + " is closed.");
                } catch (Exception e) {
                    LOGGER.error("Failed to close ApplicationContext", e);
                }

                try {
                    LOCK.lock();
                    STOP.signal();
                } finally {
                    LOCK.unlock();
                }
            }
        }));
    }

    /**
     * Keep.
     */
    public void keep() {
        synchronized (LOCK) {
            try {
                LOGGER.info("Application is keep running ... ");
                LOCK.wait();
            } catch (InterruptedException e) {
                LOGGER.error("interrupted error", e);
            }
        }
    }
}
