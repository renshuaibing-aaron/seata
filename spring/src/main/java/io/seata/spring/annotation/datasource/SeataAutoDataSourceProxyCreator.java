package io.seata.spring.annotation.datasource;

import javax.sql.DataSource;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.beans.BeansException;

/**
 * @author xingfudeshi@gmail.com
 */
public class SeataAutoDataSourceProxyCreator extends AbstractAutoProxyCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeataAutoDataSourceProxyCreator.class);
    private final String[] excludes;
    private final Advisor advisor = new DefaultIntroductionAdvisor(new SeataAutoDataSourceProxyAdvice());

    public SeataAutoDataSourceProxyCreator(boolean useJdkProxy, String[] excludes) {
        this.excludes = excludes;
        setProxyTargetClass(!useJdkProxy);
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource customTargetSource) throws BeansException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Auto proxy of [{}]", beanName);
        }
        return new Object[]{advisor};
    }

    @Override
    protected boolean shouldSkip(Class<?> beanClass, String beanName) {
        return SeataProxy.class.isAssignableFrom(beanClass) ||
            !DataSource.class.isAssignableFrom(beanClass) ||
            Arrays.asList(excludes).contains(beanClass.getName());
    }
}
