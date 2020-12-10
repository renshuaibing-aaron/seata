package io.seata.spring.boot.autoconfigure;

import io.seata.spring.annotation.GlobalTransactionScanner;
import io.seata.spring.annotation.datasource.SeataAutoDataSourceProxyCreator;
import io.seata.spring.boot.autoconfigure.properties.SeataProperties;
import io.seata.spring.boot.autoconfigure.provider.SpringApplicationContextProvider;
import io.seata.tm.api.DefaultFailureHandlerImpl;
import io.seata.tm.api.FailureHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import static io.seata.common.Constants.BEAN_NAME_FAILURE_HANDLER;
import static io.seata.common.Constants.BEAN_NAME_SPRING_APPLICATION_CONTEXT_PROVIDER;
import static io.seata.spring.annotation.datasource.AutoDataSourceProxyRegistrar.BEAN_NAME_SEATA_AUTO_DATA_SOURCE_PROXY_CREATOR;

/**
 * @author xingfudeshi@gmail.com
 * seata 自动配置类  springboot
 */
@ComponentScan(basePackages = "io.seata.spring.boot.autoconfigure.properties")
@ConditionalOnProperty(prefix = StarterConstants.SEATA_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@EnableConfigurationProperties({SeataProperties.class})
public class SeataAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeataAutoConfiguration.class);

    @Bean(BEAN_NAME_SPRING_APPLICATION_CONTEXT_PROVIDER)
    @ConditionalOnMissingBean(name = {BEAN_NAME_SPRING_APPLICATION_CONTEXT_PROVIDER})
    public SpringApplicationContextProvider springApplicationContextProvider() {
        return new SpringApplicationContextProvider();
    }

    @Bean(BEAN_NAME_FAILURE_HANDLER)
    @ConditionalOnMissingBean(FailureHandler.class)
    public FailureHandler failureHandler() {
        return new DefaultFailureHandlerImpl();
    }

    @Bean
    @DependsOn({BEAN_NAME_SPRING_APPLICATION_CONTEXT_PROVIDER, BEAN_NAME_FAILURE_HANDLER})
    @ConditionalOnMissingBean(GlobalTransactionScanner.class)
    public GlobalTransactionScanner globalTransactionScanner(SeataProperties seataProperties, FailureHandler failureHandler) {

        //todo  此处重点进行bean注入
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Automatically configure Seata");
        }
        return new GlobalTransactionScanner(seataProperties.getApplicationId(), seataProperties.getTxServiceGroup(), failureHandler);
    }



    @Bean(BEAN_NAME_SEATA_AUTO_DATA_SOURCE_PROXY_CREATOR)
    @ConditionalOnProperty(prefix = StarterConstants.SEATA_PREFIX, name = {"enableAutoDataSourceProxy", "enable-auto-data-source-proxy"}, havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(SeataAutoDataSourceProxyCreator.class)
    public SeataAutoDataSourceProxyCreator seataAutoDataSourceProxyCreator(SeataProperties seataProperties) {

        //数据源代理实现入口
        return new SeataAutoDataSourceProxyCreator(seataProperties.isUseJdkProxy(),seataProperties.getExcludesForAutoProxying());
    }
}
