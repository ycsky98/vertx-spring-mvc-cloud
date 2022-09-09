package org.rpc.client.config.tcp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.rpc.agreement.constfinal.Code;
import org.rpc.client.handler.CallBack;
import org.rpc.entity.Model;
import org.rpc.entity.SendModel;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.parsetools.RecordParser;
import org.rpc.utils.BeanNameFormat;

/**
 * @author yangcong
 * 
 *         客户端tcp启动
 */
public class Tcp {

    /**
     * net服务
     */
    private NetClient netClient;

    private String host;

    private Integer port;

    private Vertx vertx;

    /**
     * 连接的服务 k服务名称, v对应服务的tcp
     */
    private volatile Map<String, org.rpc.client.config.tcp.Tcp> clientTcps = new ConcurrentHashMap<>();

    /**
     * 连接的服务名
     */
    private String connectionServiceName;

    /**
     * 发送存储
     */
    private volatile ConcurrentHashMap<String, SendModel> concurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 并发队列
     */
    private volatile ConcurrentLinkedQueue<SendModel> sendModels = new ConcurrentLinkedQueue<>();

    /**
     * 心跳连接
     */
    private Map<String, Long> keepMap = new ConcurrentHashMap<>();

    /**
     * 心跳关键字
     */
    public static final String KEEP = "KEEP";

    /**
     * 
     * @param host
     * @param port
     * @param connectionServiceName 连接的服务名称
     * @param clientTcps            发送容器
     */
    public Tcp(String host, Integer port, Vertx vertx, String connectionServiceName,
            Map<String, org.rpc.client.config.tcp.Tcp> clientTcps) {
        this.host = host;
        this.port = port;
        this.clientTcps = clientTcps;
        this.connectionServiceName = connectionServiceName;

        this.vertx = vertx;

        // 创建客户端
        this.netClient = this.vertx.createNetClient();

        this.netClient.connect(this.port, this.host, connectHandler -> {
            // 如果连接成功
            if (connectHandler.succeeded()) {
                clientTcps.put(connectionServiceName, this);

                // 采用切割器(socket监听会进行数据切割)
//                final RecordParser parser = RecordParser.newDelimited("\n\n", buffer -> {
//                    // 收到服务端返回的心跳包
//                    if (new String(buffer.getBytes()).equals("ping")) {
//                        // 收到后重新设置值
//                        keepMap.put(KEEP, System.currentTimeMillis());
//                        return;
//                    }
//                    Model model = null;
//                    try {
//                        model = new ObjectMapper().readValue(new String(buffer.getBytes()), Model.class);
//                        // 设置回调的model
//                        concurrentHashMap.get(model.getUuid()).getCallBack().call(model);
//                        // 回调完后删除还存储在待发送的数据
//                        concurrentHashMap.remove(model.getUuid());
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    }
//                });

                /**
                 * 处理客户端的粘包器
                 */
                final org.rpc.record.RecordParser recordParser = new org.rpc.record.RecordParser(connectHandler.result()) {
                    @Override
                    public void isPing(String ping) {
                        // 收到服务端返回的心跳包
                        if ("isPing".equals(ping)) {
                            // 收到后重新设置值
                            keepMap.put(KEEP, System.currentTimeMillis());
                        }
                    }

                    /**
                     * 处理服务消息
                     * @param data
                     */
                    @Override
                    public void service(String data) {
                        return;
                    }

                    @Override
                    public void client(String result) {
                        Model model = null;
                        try {
                            model = new ObjectMapper().readValue(result, Model.class);
                            //如果数据里面有就处理
                            if (!concurrentHashMap.containsKey(model.getUuid()))
                                return;
                            // 设置回调的model
                            concurrentHashMap.get(model.getUuid()).getCallBack().call(model);
                            // 回调完后删除还存储在待发送的数据
                            concurrentHashMap.remove(model.getUuid());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                };

                //  定时器发送ping(1秒一次ping)
                this.vertx.setPeriodic(1, id -> {
                    // 有心跳存储了
                    if (keepMap.size() > 0) {
                        // 如果超时了
                        if (System.currentTimeMillis() - keepMap.get(KEEP) > 3000L) {
                            // 超时退出
                            this.vertx.cancelTimer(id);
                            this.clientTcps.put(connectionServiceName,
                                    new Tcp(this.host, this.port, this.vertx, this.connectionServiceName, this.clientTcps));
                        }
                    } else {
                        //设置心跳存储
                        keepMap.put(KEEP, System.currentTimeMillis());
                    }
                    Buffer buffer = Buffer.buffer();
                    buffer.appendInt(6);
                    buffer.appendString("isPing", "UTF-8");
                    //connectHandler.result().write(Buffer.buffer("ping\n\n", "UTF-8"));
                    connectHandler.result().write(buffer);
                    connectHandler.result().handler(recordParser);
                });

                //  定时消费队列
                this.vertx.setPeriodic(1, id -> {
                    if (sendModels.size() > 0) {
                        // 队列拿到第一个
                        SendModel send = this.sendModels.poll();
                        if (send != null) {
                            try {
                                String json = new ObjectMapper().writeValueAsString(send.getModel());
                                Buffer buffer = Buffer.buffer();
                                buffer.appendInt(json.length());
                                buffer.appendString(json, "UTF-8");
                                //connectHandler.result().write(json + "\n\n", "UTF-8");
                                connectHandler.result().write(buffer);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // 有心跳存储了
                    if (keepMap.size() > 0) {
                        // 如果超时了
                        if (System.currentTimeMillis() - keepMap.get(KEEP) > 5000L) {
                            // 超时退出(停止发送数据)
                            this.vertx.cancelTimer(id);
                        }
                    }
                    connectHandler.result().handler(recordParser);
                });

            } else if (connectHandler.failed()) {
                System.gc();
                this.clientTcps.put(connectionServiceName,
                        new Tcp(this.host, this.port, this.vertx, this.connectionServiceName, clientTcps));
            }
        });
    }

    /**
     *
     * @param objectName
     * @param methodName
     * @param args
     * @param callBack    回调函数
     * @return
     */
    public void sendService(String objectName, String methodName, Object[] args,
            CallBack callBack) {
        String uuid = UUID.randomUUID().toString().replace("-", "").replace("\"", "").replace("\\", "");
        //为了防止重复uuid(对重复的uuid进行去重)
        if (this.concurrentHashMap.containsKey(uuid)){
            //直到uuid不重复为止
            sendService(objectName, methodName, args, callBack);
            return;
        }

        Model model = new Model();
        model.setMethodName(methodName);
        model.setArgs(args);
        model.setObjectName(objectName);
        model.setUuid(uuid);

        SendModel sendModel = new SendModel();
        sendModel.setCallBack(callBack);
        sendModel.setModel(model);
        this.sendModels.add(sendModel);
        this.concurrentHashMap.put(uuid, sendModel);
    }

    /**
     * 
     * @param <T>
     * @param objectName
     * @param methodName
     * @param args
     * @return
     */
    public <T> T sendService(String objectName, String methodName, Object[] args) {
        CompletableFuture complete = new CompletableFuture<>();
        Future.future(handler -> {
            this.sendService(objectName, methodName, args, callBack -> {
                handler.complete(callBack.getResult());
            });
        }).onSuccess(result -> {
            complete.complete(result);
        });
        try {
            return (T) complete.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
