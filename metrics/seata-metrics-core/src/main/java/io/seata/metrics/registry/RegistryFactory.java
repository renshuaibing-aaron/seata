package io.seata.metrics.registry;

import java.util.Objects;

import io.seata.common.exception.NotSupportYetException;
import io.seata.common.loader.EnhancedServiceLoader;
import io.seata.common.util.StringUtils;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;

/**
 * Registry Factory for load configured metrics registry
 *
 * @author zhengyangyong
 */
public class RegistryFactory {
    public static Registry getInstance() {
        RegistryType registryType;
        String registryTypeName = ConfigurationFactory.getInstance().getConfig(
            ConfigurationKeys.METRICS_PREFIX + ConfigurationKeys.METRICS_REGISTRY_TYPE, null);
        if (!StringUtils.isNullOrEmpty(registryTypeName)) {
            try {
                registryType = RegistryType.getType(registryTypeName);
            } catch (Exception exx) {
                throw new NotSupportYetException("not support metrics registry type: " + registryTypeName);
            }
            return EnhancedServiceLoader.load(Registry.class, Objects.requireNonNull(registryType).getName());
        }
        return null;
    }
}
