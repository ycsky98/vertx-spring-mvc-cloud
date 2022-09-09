package org.rpc.service.handler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.rpc.agreement.constfinal.Code;
import org.rpc.entity.Model;
import org.rpc.utils.BeanNameFormat;
import org.springframework.context.support.AbstractApplicationContext;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

/**
 * @author yangcong
 * 
 *         连接映射器
 */
public class ConnectHandler implements Handler<NetSocket> {

    /**
     * 引入Spring上下文
     */
    private AbstractApplicationContext abstractApplicationContext;

    public ConnectHandler(AbstractApplicationContext abstractApplicationContext) {
        this.abstractApplicationContext = abstractApplicationContext;
        this.abstractApplicationContext.start();
    }

    /**
     * 有服务发送消息认为连接了
     */
    @Override
    public void handle(NetSocket netSocket) {
        //采用切割器(socket监听会进行数据切割)
//        final RecordParser parser = RecordParser.newDelimited("\n\n", buffer -> {
//            // 拿到传输的字节
//            byte[] bytes = buffer.getBytes();
//            //如果收到的是ping(不做任何事情)
//            if(new String(buffer.getBytes()).equals("ping")){
//                netSocket.write(Buffer.buffer("ping\n\n", "UTF-8"));
//                return;
//            }
//            Model model = new Model();
//            try {
//                // 服务端读取json
//                model = new ObjectMapper().readValue(new String(bytes), Model.class);
//                // 拿到要执行的对象
//                Object obj = abstractApplicationContext.getBean(BeanNameFormat.beanFormat(model.getObjectName()));
//
//                Method method = null;
//                // 拿到方法
//                method = obj.getClass().getMethod(model.getMethodName(), model.getTypes());
//
//                // 拿到执行后的返回值
//                model.setResult(method.invoke(obj, model.getArgs()));
//                model.setCode(Code.SUCCED_CODE);
//
//                // 将结果吐回
//                netSocket.write(Buffer.buffer(new ObjectMapper().writeValueAsString(model) + "\n\n", "UTF-8"));
//                // 以下是出异常后返回的信息
//            } catch (Throwable e) {
//                InvocationTargetException invocationTargetException = (InvocationTargetException) e;
//                e.printStackTrace();
//                // 转换异常
//                model.setCode(Code.ERROR_CODE);
//                model.setResult(invocationTargetException.getTargetException().getMessage());
//                // 将异常吐回
//                try {
//                    netSocket.write(Buffer.buffer(new ObjectMapper().writeValueAsString(model) + "\n\n", "UTF-8"));
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });

        /**
         * 粘包处理器
         */
        final org.rpc.record.RecordParser recordParser = new org.rpc.record.RecordParser(netSocket) {

            /**
             * 接受到ping请求
             *
             * @param ping
             */
            @Override
            public void isPing(String ping) {
                Buffer buffer = Buffer.buffer();
                buffer.appendInt(6).appendString("isPing");
                this.getNetSocket().write(buffer);
            }

            /**
             * 处理服务消息
             *
             * @param data
             */
            @Override
            public void service(String data) {
                Buffer buffer = Buffer.buffer();
                String result = "";
                Model model = new Model();
                try {
                    // 服务端读取json
                    model = new ObjectMapper().readValue(data, Model.class);
                    // 拿到要执行的对象
                    Object obj = abstractApplicationContext.getBean(BeanNameFormat.beanFormat(model.getObjectName()));

                    Method method = null;
                    // 拿到方法
                    method = obj.getClass().getMethod(model.getMethodName(), model.getTypes());

                    // 拿到执行后的返回值
                    model.setResult(method.invoke(obj, model.getArgs()));
                    model.setCode(Code.SUCCED_CODE);

                    result = new ObjectMapper().writeValueAsString(model);
                    buffer.appendInt(result.length());
                    buffer.appendString(result);
                    this.getNetSocket().setWriteQueueMaxSize(buffer.length());
                    // 将结果吐回
                    this.getNetSocket().write(buffer);
                    // 以下是出异常后返回的信息
                } catch (Throwable e) {
                    InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                    e.printStackTrace();
                    // 转换异常
                    model.setCode(Code.ERROR_CODE);
                    model.setResult(invocationTargetException.getTargetException().getMessage());

                    // 将异常吐回
                    try {
                        result = new ObjectMapper().writeValueAsString(model);
                        buffer.appendInt(result.length());
                        buffer.appendString(result);
                        this.getNetSocket().setWriteQueueMaxSize(buffer.length());
                        this.getNetSocket().write(buffer);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            @Override
            public void client(String result) {

            }
        };
        netSocket.handler(recordParser);
    }
}
