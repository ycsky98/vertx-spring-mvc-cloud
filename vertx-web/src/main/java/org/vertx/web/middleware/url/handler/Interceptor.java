package org.vertx.web.middleware.url.handler;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author yangcong
 * 
 * 拦截器
 */
public interface Interceptor {
    
    /**
     * 返回true则通过, false则拒绝通过
     * @param request
     * @param response
     * @return
     */
    public boolean intercept(HttpServerRequest request, HttpServerResponse response);
}
