package io.seata.core.protocol.transaction;


import io.seata.core.exception.TransactionExceptionCode;
import io.seata.core.protocol.AbstractResultMessage;

/**
 * The type Abstract transaction response.
 *
 * @author sharajava
 */
public abstract class AbstractTransactionResponse extends AbstractResultMessage {

    private TransactionExceptionCode transactionExceptionCode = TransactionExceptionCode.Unknown;

    /**
     * Gets transaction exception code.
     *
     * @return the transaction exception code
     */
    public TransactionExceptionCode getTransactionExceptionCode() {
        return transactionExceptionCode;
    }

    /**
     * Sets transaction exception code.
     *
     * @param transactionExceptionCode the transaction exception code
     */
    public void setTransactionExceptionCode(TransactionExceptionCode transactionExceptionCode) {
        this.transactionExceptionCode = transactionExceptionCode;
    }

}
