package org.vertx.web.middleware.url.handler.path;

import org.vertx.web.config.WebConfig;
import org.vertx.web.middleware.url.handler.Interceptor;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author yangcong
 * 
 *      路径拦截器
 */
public class PathInterceptor implements Interceptor{

    private WebConfig webConfig;

    /**
     * 构造器传入RouterContext上下文
     * 
     * @param routingContext
     */
    public PathInterceptor(WebConfig webConfig){
        this.webConfig = webConfig;
    }

    /**
     * 拦截主方法
     */
    @Override
    public boolean intercept(HttpServerRequest request, HttpServerResponse response) {
        return pathReg(request.path());
    }

    /**
     * 路径匹配
     * @param url
     * @return
     */
    public boolean pathReg(String url){
        //step1 获取所有的url地址（等待扫描时补充）
        return this.webConfig.getUrl().containsKey(url);
    }

}
