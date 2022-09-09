package org.rpc.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author yangcong
 * 
 *      传输模型
 */
public class Model implements Serializable{

    private static final long serialVersionUID = 3192148192144680675L;

    /**
     * 状态码
     */
    private int code;
    
    /**
     * 执行的参数
     */
    private Object[] args;

    /**
     * 对象名称
     */
    private String objectName;

    /**
     * 执行的方法
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] types;

    /**
     * 执行后的结果值
     */
    private Object result;

    /**
     * 唯一编号
     */
    private String uuid;

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(final Object[] args) {
        this.args = args;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(final Object result) {
        this.types = null;
        this.result = result;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        //不为空
        if(!Objects.isNull(args)){
            types = new Class<?>[args.length];
            //设置类型
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
        }
        return types;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(args);
        result = prime * result + code;
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
        result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + Arrays.hashCode(types);
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Model other = (Model) obj;
        if (!Arrays.deepEquals(args, other.args))
            return false;
        if (code != other.code)
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (objectName == null) {
            if (other.objectName != null)
                return false;
        } else if (!objectName.equals(other.objectName))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        if (!Arrays.equals(types, other.types))
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Model [args=" + Arrays.toString(args) + ", code=" + code + ", methodName=" + methodName
                + ", objectName=" + objectName + ", result=" + result + ", types=" + Arrays.toString(types) + ", uuid="
                + uuid + "]";
    }
    
}
