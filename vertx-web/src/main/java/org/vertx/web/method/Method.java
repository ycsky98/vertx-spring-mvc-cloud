package org.vertx.web.method;

/**
 * @author yangcong
 * 
 *      请求类型
 */
public enum Method {
    
    GET("GET"), POST("POST");

    private String type;

    Method(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
