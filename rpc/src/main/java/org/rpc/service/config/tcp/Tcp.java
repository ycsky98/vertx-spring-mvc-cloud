package org.rpc.service.config.tcp;

import org.rpc.service.handler.ConnectHandler;
import org.springframework.context.support.AbstractApplicationContext;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

/**
 * @author yangcong
 * 
 *         服务端tcp创建
 */
public class Tcp {

    /**
     * net服务
     */
    private NetServer netServer;

    private String host;

    private Integer port;

    public Tcp(String host, Integer port, AbstractApplicationContext abstractApplicationContext) {
        this.netServer = Vertx.vertx().createNetServer();

        // 由ConnectHandler处理接受到的数据
        this.netServer.connectHandler(new ConnectHandler(abstractApplicationContext));

        this.netServer.listen(port, host, new Handler<AsyncResult<NetServer>>() {
            @Override
            public void handle(AsyncResult<NetServer> event) {
                if (event.succeeded()) {
                    // 成功
                    System.out.println("RPC Server start success");
                } else if (event.failed()) {
                    // 失败
                    System.out.println(event.cause().getMessage());
                }
            }
        });
    }
}
