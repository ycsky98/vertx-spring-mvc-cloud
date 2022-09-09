package org.rpc.record;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

/**
 * @author yangcong
 *
 * 自定义粘包处理器
 */
public abstract class RecordParser implements Handler<Buffer> {

    /**
     * 数据包大小
     */
    private int size = -1;

    /**
     * 游标起始位置
     */
    private int start = 0;

    /**
     * 尾部游标
     */
    private int end = 0;

    /**
     * 缓冲区大小
     */
    private static final Integer CACHE_SIZE = 1048576;

    /**
     * Buffer缓存(1MB)
     */
    private Buffer cacheBuffer = Buffer.buffer(CACHE_SIZE);

    /**
     * 记录缓冲区是否有数据
     */
    private boolean flag = false;

    private NetSocket netSocket;

    public RecordParser(NetSocket netSocket){
        this.netSocket = netSocket;
    }

    @Override
    public void handle(Buffer buffer) {
        String data = "";
        //缓存数据
        this.cacheBuffer.appendBuffer(buffer);

        //长度小于4
        if (this.cacheBuffer.length() < 4){
            this.cacheBuffer.appendBuffer(buffer);
            //返回,让后面的数据继续进来
            return;
        }

        while (true){
            //拿到当前数据包的大小
            this.size = this.cacheBuffer.getInt(this.start);

            //移动起始游标
            this.start += 4;

            //表示数据刚好够被截取
            if (this.cacheBuffer.length() - this.size - this.start >= 0){

                //确定尾部游标
                this.end = this.start + this.size;

                //判定是否到了末尾
                if (this.cacheBuffer.length() - this.end <= 4){
                    //拿到数据
                    data = this.cacheBuffer.getString(this.start, this.end);
                    if ("isPing".equals(data)){
                        this.isPing(data);
                    }else {
                        this.service(data);
                        this.client(data);
                    }
                    //将缓存缩容
                    this.cacheBuffer = Buffer.buffer(this.cacheBuffer.getBuffer(this.end, this.cacheBuffer.length()).getByteBuf());
                    break;
                }

                //判定是否超出(尾部大于缓存长度)
                if (this.cacheBuffer.length() < this.end){
                    //将缓存缩容
                    this.cacheBuffer = Buffer.buffer(this.cacheBuffer.getBuffer(this.start - 4, this.cacheBuffer.length()).getByteBuf());
                    break;
                }

                //拿到数据
                data = this.cacheBuffer.getString(this.start, this.end);

                //将游标并行
                this.start = this.end;

                if ("isPing".equals(data)){
                    this.isPing(data);
                }else {
                    this.service(data);
                    this.client(data);
                }
                //缓存的数据处理完了
                if (this.cacheBuffer.length() == this.size){
                    this.cacheBuffer = Buffer.buffer(CACHE_SIZE);
                    System.gc();
                    break;
                }
            }else {
                break;
            }
        }

        //下标重置
        this.reset();
    }

    /**
     * 重置游标
     */
    public void reset(){
        this.size = -1;
        this.start = 0;
    }

    /**
     * 对于ping 的处理
     * @param ping
     */
    public abstract void isPing(String ping);

    /**
     * 对于服务端的处理
     * @param data
     */
    public abstract void service(String data);

    /**
     * 对于客户端的处理
     * @param result
     */
    public abstract void client(String result);

    public NetSocket getNetSocket(){
        return this.netSocket;
    }
}
