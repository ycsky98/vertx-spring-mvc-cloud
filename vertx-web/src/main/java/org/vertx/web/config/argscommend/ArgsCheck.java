package org.vertx.web.config.argscommend;

import com.beust.jcommander.JCommander;

/**
 * @author yangcong
 *
 * 参数校验
 */
public class ArgsCheck {

    private String[] args;

    public ArgsCheck(String[] args){
        this.args = args;
    }

    /**
     * 对对象进行参数校验
     */
    public ArgsCheck check(Object object){
        JCommander.newBuilder().addObject(object).build().parse(this.args);
        return this;
    }
}
