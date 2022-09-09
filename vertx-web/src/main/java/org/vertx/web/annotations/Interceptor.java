package org.vertx.web.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author yangcong
 * 
 *      拦截器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface Interceptor {

    /**
     * 需要拦截的url
     * @return
     */
    String url() default "";

    /**
     * 正则拦截
     * @return
     */
    String regex() default "";
}
