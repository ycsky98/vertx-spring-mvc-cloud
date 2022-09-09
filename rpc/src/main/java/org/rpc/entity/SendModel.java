package org.rpc.entity;

import org.rpc.client.handler.CallBack;

/**
 * @author yangcong
 *      发送模型
 */
public class SendModel {
    
    private CallBack callBack;

    private Model model;

    private int code = 0;

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
}
