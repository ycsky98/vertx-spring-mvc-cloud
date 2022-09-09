package org.vertx.web.config;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.beust.jcommander.JCommander;
import org.springframework.context.support.AbstractApplicationContext;
import org.vertx.web.annotations.rpc.RpcConnection;
import org.vertx.web.annotations.rpc.RpcServersConnection;
import org.vertx.web.annotations.rpc.Server;

import io.vertx.core.Vertx;
import org.vertx.web.config.nacos.po.NacosConfig;
import org.vertx.web.config.nacos.sdk.ConfigService;
import org.vertx.web.config.nacos.sdk.RegisterService;

/**
 * @author yangcong
 * <p>
 * Rpc配置类
 */
public class RpcConfig {

    /**
     * spring上下文
     */
    private AbstractApplicationContext abstractApplicationContext;

    /**
     * 当前服务端的tcp
     */
    private org.rpc.service.config.tcp.Tcp serverTcp;

    /**
     * 连接的服务 k服务名称, v对应服务的tcp
     */
    private static Map<String, org.rpc.client.config.tcp.Tcp> clientTcps = new ConcurrentHashMap<>();

    /**
     * Nacos启动参数
     */
    private NacosConfig nacosConfig;

    /**
     * Nacos配置服务
     */
    private ConfigService configService;

    /**
     * Nacos注册服务
     */
    private RegisterService registerService;

    /**
     * @param abstractApplicationContext
     */
    public RpcConfig(AbstractApplicationContext abstractApplicationContext, NacosConfig nacosConfig) {
        this.abstractApplicationContext = abstractApplicationContext;
        this.nacosConfig = nacosConfig;

        //配置了Server才去连Nacos
        if (this.abstractApplicationContext.getBeansWithAnnotation(Server.class).size() == 1){
            try {
                Properties nacosProperties = new Properties();
                nacosProperties.setProperty("serverAddr", this.nacosConfig.getAddress());
                nacosProperties.setProperty("username", this.nacosConfig.getUsername());
                nacosProperties.setProperty("password", this.nacosConfig.getPassword());
                //开启Nacos服务
                this.configService = new ConfigService(nacosProperties);
                this.registerService = new RegisterService(nacosProperties);
            } catch (NacosException nacosException) {
                throw new RuntimeException(nacosException.getMessage());
            }
            // 启动server
            this.serverStart();
        }
        // 连接远程服务
        this.connectionRemoteServer();
    }

    /**
     * 启动当前系统服务
     */
    private void serverStart() {
        Map<String, Object> beans = this.abstractApplicationContext.getBeansWithAnnotation(Server.class);
        //如果没有连接的,就不连
        if (beans.size() == 0) {
            return;
        }
        if (beans.size() != 1) {
            throw new RuntimeException("当前服务地址存在多个,请检查配置项!!");
        }
        // 拿到server
        Set<Map.Entry<String, Object>> servers = beans.entrySet();
        Server server = null;
        for (Map.Entry<String, Object> entry : servers) {// 拿出对应的注解
            server = entry.getValue().getClass().getAnnotation(Server.class);
            break;
        }
        // 启动当前服务
        this.serverTcp = new org.rpc.service.config.tcp.Tcp(server.host(), server.port(),
                this.abstractApplicationContext);
        //将服务注入Nacos做可视化管理
        Instance instance = new Instance();
        //设置健康状态
        instance.setHealthy(true);
        //设置服务名称
        instance.setServiceName(server.serverName());
        //设置自动
        instance.setEnabled(true);
        //将服务注册到Nacos中
        this.registerService.serviceRegister(instance);
    }

    /**
     * 连接远程服务
     */
    private void connectionRemoteServer() {
        Map<String, Object> beans = this.abstractApplicationContext.getBeansWithAnnotation(RpcServersConnection.class);
        if (beans.size() == 0) {
            return;
        }
        if (beans.size() != 1) {
            throw new RuntimeException("当前服务地址存在多个,请检查配置项!!");
        }
        // 拿到RemoteServer
        Set<Map.Entry<String, Object>> servers = beans.entrySet();
        RpcServersConnection rpcServersConnection = null;
        RpcConnection[] rpcConnections = null;

        // 遍历服务
        for (Map.Entry<String, Object> entry : servers) {
            rpcServersConnection = entry.getValue().getClass().getAnnotation(RpcServersConnection.class);
            rpcConnections = rpcServersConnection.rpcConnections();
            for (int i = 0; i < rpcConnections.length; i++) {
                new org.rpc.client.config.tcp.Tcp(rpcConnections[i].host(), rpcConnections[i].port(), Vertx.vertx(),
                        rpcConnections[i].serviceName(), clientTcps);
            }
        }
    }

    /**
     * 获取对应服务的rpc
     *
     * @param serviceName 获取调用的服务名称
     * @return
     */
    public static org.rpc.client.config.tcp.Tcp getService(String serviceName) {
        org.rpc.client.config.tcp.Tcp tcp = RpcConfig.clientTcps.get(serviceName);
        if (tcp == null)
            throw new RuntimeException("service connection is null");
        return tcp;
    }
}
