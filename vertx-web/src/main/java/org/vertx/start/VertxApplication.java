package org.vertx.start;

import org.vertx.init.impl.InitImpl;
import org.vertx.web.config.nacos.po.NacosConfig;

/**
 * @author yangcong
 * <p>
 * 启动类
 */
public class VertxApplication {

    /**
     * @param configs
     * @param args    启动参数
     */
    public static void start(int port, String[] args, String... configs) {
        // 启动服务器
        new InitImpl().start(args, port, configs);
    }
}
