package io.seata.tm;

import io.seata.core.rpc.netty.TmRpcClient;

/**
 * The type Tm client.
 *
 * @author slievrly
 */
public class TMClient {

    /**
     * Init.
     *
     * @param applicationId           the application id
     * @param transactionServiceGroup the transaction service group
     */
    public static void init(String applicationId, String transactionServiceGroup) {
        TmRpcClient tmRpcClient = TmRpcClient.getInstance(applicationId, transactionServiceGroup);
        tmRpcClient.init();
    }

}
