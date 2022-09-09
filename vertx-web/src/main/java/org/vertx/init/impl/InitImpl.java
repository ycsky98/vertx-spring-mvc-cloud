package org.vertx.init.impl;

import org.vertx.init.InitInterface;
import org.vertx.init.vertx.AbstractVerticleImpl;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.vertx.web.config.nacos.po.NacosConfig;

/**
 * 初始化类
 *
 * @author yangcong
 */
public class InitImpl implements InitInterface {

    /**
     * @param args    启动参数
     * @param port
     * @param configs
     */
    @Override
    public void start(String[] args, int port, String... configs) {
        Vertx.vertx().deployVerticle(new AbstractVerticleImpl(args, port, configs),
                new DeploymentOptions().setMaxWorkerExecuteTime(2000));
    }
}
