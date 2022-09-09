package org.vertx.web.annotations.rpc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author yangcong
 * 
 *         标注单个服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface RpcConnection {

    /**
     * 远程的服务名称
     * 
     * @return
     */
    String serviceName();

    int port() default 80;

    String host();
}
