package io.seata.rm;

import io.seata.core.rpc.netty.RmMessageListener;
import io.seata.core.rpc.netty.RmRpcClient;

/**
 * The Rm client Initiator.
 *
 * @author slievrly
 */
public class RMClient {

    /**
     * Init.
     *
     * @param applicationId           the application id
     * @param transactionServiceGroup the transaction service group
     */
    public static void init(String applicationId, String transactionServiceGroup) {
        // 获取单例对象
        RmRpcClient rmRpcClient = RmRpcClient.getInstance(applicationId, transactionServiceGroup);
        // 设置ResourceManager的单例对象
        rmRpcClient.setResourceManager(DefaultResourceManager.get());
        // 添加监听器，监听Server端的消息推送
        //这里可以看出和TM客户端的不一样的地方 这个地方增加一个监听器保证
        //RM除了主动操作本地资源外，还会因为全局事务的commit、rollback等的消息推送，从而对本地资源进行相关操作
        rmRpcClient.setClientMessageListener(new RmMessageListener(DefaultRMHandler.get(), rmRpcClient));
        // 初始化RPC
        rmRpcClient.init();
    }

}
