package org.vertx.web.config.nacos.sdk;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.vertx.web.config.nacos.AbstractNacos;

import java.util.Properties;

/**
 * @author yangcong
 * <p>
 * 配置服务
 */
public class ConfigService extends AbstractNacos {

    /**
     * 构建Nacos配置
     *
     * @param properties
     * @throws NacosException
     */
    public ConfigService(Properties properties) throws NacosException {
        super(properties);
    }

    /**
     * 配置推送
     *
     * @param dataId
     * @param group
     * @param content
     */
    public boolean pushConfig(String dataId, String group, String content) {
        try {
            super.getConfigService().publishConfig(dataId, group, content);
        } catch (NacosException nacosException) {
            throw new RuntimeException(nacosException.getMessage());
        }
        return true;
    }

    /**
     * 监听配置
     *
     * @param dataId
     * @param group
     * @param listener
     */
    public void listenerConfig(String dataId, String group, Listener listener) {
        try {
            super.getConfigService().addListener(dataId, group, listener);
        } catch (NacosException nacosException) {
            throw new RuntimeException(nacosException.getMessage());
        }
    }
}
