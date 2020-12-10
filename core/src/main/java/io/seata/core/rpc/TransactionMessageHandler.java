package io.seata.core.rpc;

import io.seata.core.protocol.AbstractMessage;
import io.seata.core.protocol.AbstractResultMessage;

/**主要在上层处理接收到的RPC消息。有onRequest、onResponse两个接口
 * To handle the received RPC message on upper level.
 *
 * @author slievrly
 */
public interface TransactionMessageHandler {

    /**
     * On a request received.
     *
     * @param request received request message
     * @param context context of the RPC
     * @return response to the request
     */
    AbstractResultMessage onRequest(AbstractMessage request, RpcContext context);

    /**
     * On a response received.
     *
     * @param response received response message
     * @param context  context of the RPC
     */
    void onResponse(AbstractResultMessage response, RpcContext context);

}
