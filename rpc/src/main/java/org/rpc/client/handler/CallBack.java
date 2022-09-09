package org.rpc.client.handler;

import org.rpc.entity.Model;

/**
 * @author yangcong
 * 
 *      回调函数
 */
public interface CallBack {
    
    public void call(Model model);
}
