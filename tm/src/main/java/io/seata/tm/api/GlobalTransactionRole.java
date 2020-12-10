package io.seata.tm.api;

/**
 * Role of current thread involve in a global transaction.
 *
 * @author sharajava
 */
public enum GlobalTransactionRole {

    /**
     * The Launcher.
     */
    // The one begins the current global transaction.
    Launcher,

    /**
     * The Participant.
     */
    // The one just joins into a existing global transaction.
    Participant
}
