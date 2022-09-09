package org.vertx.init;

import org.vertx.web.config.nacos.po.NacosConfig;

/**
 * 初始化接口
 *
 * @author yangcong
 */
public interface InitInterface {

    /**
     * 启动方法
     */
    public void start(String[] args, int port, String... configs);
}
