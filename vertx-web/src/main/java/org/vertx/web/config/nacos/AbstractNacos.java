package org.vertx.web.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

import java.util.Properties;

/**
 * @author yangcong
 * <p>
 * nacos基类
 */
public abstract class AbstractNacos {

    /**
     * 配置
     */
    private ConfigService configService;

    /**
     * 注册
     */
    private NamingService namingService;

    /**
     * 构建Nacos配置
     *
     * @param properties
     * @throws NacosException
     */
    public AbstractNacos(Properties properties) throws NacosException {
        this.configService = NacosFactory.createConfigService(properties);
        this.namingService = NamingFactory.createNamingService(properties);
    }

    public ConfigService getConfigService() {
        return configService;
    }

    public NamingService getNamingService() {
        return namingService;
    }
}
