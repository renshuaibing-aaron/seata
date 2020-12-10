package io.seata.tm.api;

import io.seata.core.exception.TransactionException;
import io.seata.core.model.GlobalStatus;
import io.seata.tm.api.transaction.SuspendedResourcesHolder;

/**
 * Global transaction.
 *
 * @author sharajava
 */
public interface GlobalTransaction {

    /**
     * Begin a new global transaction with default timeout and name.
     *
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void begin() throws TransactionException;

    /**
     * Begin a new global transaction with given timeout and default name.
     *
     * @param timeout Global transaction timeout in MILLISECONDS
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void begin(int timeout) throws TransactionException;

    /**
     * Begin a new global transaction with given timeout and given name.
     *
     * @param timeout Given timeout in MILLISECONDS.
     * @param name    Given name.
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void begin(int timeout, String name) throws TransactionException;

    /**
     * Commit the global transaction.
     *
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void commit() throws TransactionException;

    /**
     * Rollback the global transaction.
     *
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void rollback() throws TransactionException;

    /**
     * Suspend the global transaction.
     *
     * @param unbindXid if true,suspend the global transaction.
     * @return the SuspendedResourcesHolder which holds the suspend resources
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * @see SuspendedResourcesHolder
     */
    SuspendedResourcesHolder suspend(boolean unbindXid) throws TransactionException;

    /**
     * Resume the global transaction.
     *
     * @param suspendedResourcesHolder the suspended resources to resume
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     * @see SuspendedResourcesHolder
     */
    void resume(SuspendedResourcesHolder suspendedResourcesHolder) throws TransactionException;

    /**
     * Ask TC for current status of the corresponding global transaction.
     *
     * @return Status of the corresponding global transaction.
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     * @see GlobalStatus
     */
    GlobalStatus getStatus() throws TransactionException;

    /**
     * Get XID.
     *
     * @return XID. xid
     */
    String getXid();

    /**
     * report the global transaction status.
     *
     * @param globalStatus global status.
     *
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     * out.
     */
    void globalReport(GlobalStatus globalStatus) throws TransactionException;

    /**
     * local status of the global transaction.
     *
     * @return Status of the corresponding global transaction.
     * @see GlobalStatus
     */
    GlobalStatus getLocalStatus();
}
