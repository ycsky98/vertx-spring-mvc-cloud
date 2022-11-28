package org.vertx.demo;

import org.vertx.start.VertxApplication;
import org.vertx.web.annotations.rpc.RpcConnection;
import org.vertx.web.annotations.rpc.RpcServersConnection;
import org.vertx.web.annotations.rpc.Server;

/**
 * @author yangcong
 * <p>
 * 使用demo
 */
// 本地服务
@Server(serverName = "testServer", host = "localhost", port = 8888)
// 远程服务调用配置
//@RpcServersConnection(rpcConnections = {@RpcConnection(serviceName = "test2Server", host = "localhost", port = 8889)})
public class Test {

    public static void main(String[] args) {
        // 启动对于http调用的服务
        VertxApplication.start(80, args, "spring.xml");
    }
}
