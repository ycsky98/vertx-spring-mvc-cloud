package org.vertx.init.vertx;

import org.vertx.web.config.WebConfig;

import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.core.AbstractVerticle;
import org.vertx.web.config.argscommend.ArgsCheck;
import org.vertx.web.config.nacos.po.NacosConfig;

/**
 * @author yangcong
 * <p>
 * 启动
 */
public class AbstractVerticleImpl extends AbstractVerticle {

    /**
     * 配置文件组
     */
    private String[] configs;

    /**
     * 默认端口80
     */
    private int port = 80;

    /**
     * 启动参数
     */
    private String[] args;

    /**
     * Nacos配置
     */
    private NacosConfig nacosConfig = new NacosConfig();

    public AbstractVerticleImpl(String[] args, int port, String... configs) {
        this.port = port;
        this.configs = configs;
        this.args = args;
        //走参数校验
        new ArgsCheck(args).check(this.nacosConfig);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // 将初始化操作放到vertx线程池做异步启动
        vertx.executeBlocking(future -> {
            //.............................
            WebConfig webConfig = new WebConfig(super.vertx, this.nacosConfig, this.configs);
            future.complete(webConfig.getRouter());
        }, res -> {
            if (res.succeeded()){
                //启动http服务
                HttpServer server = super.vertx.createHttpServer();
                server.requestHandler(((Router) res.result())).listen(this.port).onSuccess(handler -> {
                    System.out.println("Server is start");
                });
            }
        });
    }
}
