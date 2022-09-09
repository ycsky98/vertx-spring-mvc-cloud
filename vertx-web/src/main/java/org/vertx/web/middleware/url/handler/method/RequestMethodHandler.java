package org.vertx.web.middleware.url.handler.method;

import org.vertx.web.annotations.RequestMappping;
import org.vertx.web.config.WebConfig;
import org.vertx.web.method.Method;
import org.vertx.web.middleware.url.handler.Interceptor;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author yangcong
 * 
 *      请求方法拦截器
 * 
 * <p>目前只拦截POST和GET
 */
public class RequestMethodHandler implements Interceptor{

    private WebConfig webConfig;

    public RequestMethodHandler(WebConfig webConfig){
        this.webConfig = webConfig;
    }

    @Override
    public boolean intercept(HttpServerRequest request, HttpServerResponse response) {
        String path = request.path();
        Method method = this.webConfig.getUrl().get(path).getMethod().getAnnotation(RequestMappping.class).type();
        return request.method().toString().toUpperCase().equals(method.getType().toUpperCase());
    }
    
}
