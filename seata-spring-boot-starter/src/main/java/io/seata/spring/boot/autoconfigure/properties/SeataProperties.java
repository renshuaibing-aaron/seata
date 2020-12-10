package io.seata.spring.boot.autoconfigure.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static io.seata.spring.boot.autoconfigure.StarterConstants.SEATA_PREFIX;

/**
 * @author xingfudeshi@gmail.com
 */
@ConfigurationProperties(prefix = SEATA_PREFIX)
@EnableConfigurationProperties(SpringCloudAlibabaConfiguration.class)
public class SeataProperties {
    /**
     * whether enable auto configuration
     */
    private boolean enabled = true;
    /**
     * application id
     */
    private String applicationId;
    /**
     * transaction service group
     */
    private String txServiceGroup;
    /**
     * Whether enable auto proxying of datasource bean
     */
    private boolean enableAutoDataSourceProxy = true;
    /**
     * Whether use JDK proxy instead of CGLIB proxy
     */
    private boolean useJdkProxy = false;
    /**
     * Specifies which datasource bean are not eligible for auto-proxying
     */
    private String[] excludesForAutoProxying = {};

    @Autowired
    private SpringCloudAlibabaConfiguration springCloudAlibabaConfiguration;

    public boolean isEnabled() {
        return enabled;
    }

    public SeataProperties setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getApplicationId() {
        if (null == applicationId) {
            applicationId = springCloudAlibabaConfiguration.getApplicationId();
        }
        return applicationId;
    }

    public SeataProperties setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getTxServiceGroup() {
        if (null == txServiceGroup) {
            txServiceGroup = springCloudAlibabaConfiguration.getTxServiceGroup();
        }
        return txServiceGroup;
    }

    public SeataProperties setTxServiceGroup(String txServiceGroup) {
        this.txServiceGroup = txServiceGroup;
        return this;
    }

    public boolean isEnableAutoDataSourceProxy() {
        return enableAutoDataSourceProxy;
    }

    public SeataProperties setEnableAutoDataSourceProxy(boolean enableAutoDataSourceProxy) {
        this.enableAutoDataSourceProxy = enableAutoDataSourceProxy;
        return this;
    }

    public boolean isUseJdkProxy() {
        return useJdkProxy;
    }

    public SeataProperties setUseJdkProxy(boolean useJdkProxy) {
        this.useJdkProxy = useJdkProxy;
        return this;
    }

    public String[] getExcludesForAutoProxying() {
        return excludesForAutoProxying;
    }

    public SeataProperties setExcludesForAutoProxying(String[] excludesForAutoProxying) {
        this.excludesForAutoProxying = excludesForAutoProxying;
        return this;
    }
}
