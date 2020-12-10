package io.seata.core.model;

import io.seata.core.exception.TransactionException;

/**
 * Resource Manager.
 *用于控制分支事务的提交、回滚
 * Control a branch transaction commit or rollback.
 *
 * @author sharajava
 */
public interface ResourceManagerInbound {

    /**
     * Commit a branch transaction.
     * TM通知RM提交事务
     * @param branchType      the branch type
     * @param xid             Transaction id.
     * @param branchId        Branch id.
     * @param resourceId      Resource id.
     * @param applicationData Application data bind with this branch.
     * @return Status of the branch after committing.
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     *                              out.
     */
    BranchStatus branchCommit(BranchType branchType, String xid, long branchId, String resourceId, String applicationData) throws TransactionException;

    /**
     * Rollback a branch transaction.
     * TM通知RM回滚事务
     * @param branchType      the branch type
     * @param xid             Transaction id.
     * @param branchId        Branch id.
     * @param resourceId      Resource id.
     * @param applicationData Application data bind with this branch.
     * @return Status of the branch after rollbacking.
     * @throws TransactionException Any exception that fails this will be wrapped with TransactionException and thrown
     *                              out.
     */
    BranchStatus branchRollback(BranchType branchType, String xid, long branchId, String resourceId, String applicationData) throws TransactionException;
}
