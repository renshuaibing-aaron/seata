package io.seata.core.model;

import java.util.HashMap;
import java.util.Map;

import io.seata.common.exception.ShouldNeverHappenException;

/**
 * Status of branch transaction.
 *
 * @author sharajava
 */
public enum BranchStatus {

    /**
     * The Unknown.
     * description:Unknown branch status.
     */
    Unknown(0),

    /**
     * The Registered.
     * description:Registered to TC.
     */
    Registered(1),

    /**
     * The Phase one done.
     * description:Branch logic is successfully done at phase one.
     */
    PhaseOne_Done(2),

    /**
     * The Phase one failed.
     * description:Branch logic is failed at phase one.
     */
    PhaseOne_Failed(3),

    /**
     * The Phase one timeout.
     * description:Branch logic is NOT reported for a timeout.
     */
    PhaseOne_Timeout(4),

    /**
     * The Phase two committed.
     * description:Commit logic is successfully done at phase two.
     */
    PhaseTwo_Committed(5),

    /**
     * The Phase two commit failed retryable.
     * description:Commit logic is failed but retryable.
     */
    PhaseTwo_CommitFailed_Retryable(6),

    /**
     * The Phase two commit failed unretryable.
     * description:Commit logic is failed and NOT retryable.
     */
    PhaseTwo_CommitFailed_Unretryable(7),

    /**
     * The Phase two rollbacked.
     * description:Rollback logic is successfully done at phase two.
     */
    PhaseTwo_Rollbacked(8),

    /**
     * The Phase two rollback failed retryable.
     * description:Rollback logic is failed but retryable.
     */
    PhaseTwo_RollbackFailed_Retryable(9),

    /**
     * The Phase two rollback failed unretryable.
     * description:Rollback logic is failed but NOT retryable.
     */
    PhaseTwo_RollbackFailed_Unretryable(10);

    private int code;

    BranchStatus(int code) {
        this.code = code;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    private static final Map<Integer, BranchStatus> MAP = new HashMap<>(values().length);

    static {
        for (BranchStatus status : values()) {
            MAP.put(status.getCode(), status);
        }
    }

    /**
     * Get branch status.
     *
     * @param code the code
     * @return the branch status
     */
    public static BranchStatus get(byte code) {
        return get((int)code);
    }

    /**
     * Get branch status.
     *
     * @param code the code
     * @return the branch status
     */
    public static BranchStatus get(int code) {
        BranchStatus status = MAP.get(code);

        if (null == status) {
            throw new ShouldNeverHappenException("Unknown BranchStatus[" + code + "]");
        }

        return status;
    }
}
