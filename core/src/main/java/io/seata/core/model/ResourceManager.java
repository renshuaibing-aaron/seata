package io.seata.core.model;

import java.util.Map;

/**
 * Resource Manager: common behaviors.
 *  负责管理分支数据资源事务
 * @author sharajava
 */
public interface ResourceManager extends ResourceManagerInbound, ResourceManagerOutbound {

    /**
     * Register a Resource to be managed by Resource Manager.
     *  注册一个resource至事务管理器上
     * @param resource The resource to be managed.
     */
    void registerResource(Resource resource);

    /**
     * Unregister a Resource from the Resource Manager.
     *从事务管理器上取消注册一个resource
     * @param resource The resource to be removed.
     */
    void unregisterResource(Resource resource);

    /**
     * Get all resources managed by this manager.
     * 获取所有管理的resource
     * @return resourceId -> Resource Map
     */
    Map<String, Resource> getManagedResources();

    /**
     * Get the BranchType.
     * 获取此事务管理器的分支类型，有AT自动和TCC手动类型
     * @return The BranchType of ResourceManager.
     */
    BranchType getBranchType();
}
