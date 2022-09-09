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
 *         当前系统服务启动注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface Server {

    /**
     * 服务名称
     * 
     * @return
     */
    String serverName();

    /**
     * 本机地址
     * 
     * @return
     */
    String host();

    /**
     * port端口
     * 
     * @return
     */
    int port() default 80;
}
