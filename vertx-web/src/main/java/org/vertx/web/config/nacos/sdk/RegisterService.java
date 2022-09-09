package org.vertx.web.config.nacos.sdk;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.vertx.web.config.nacos.AbstractNacos;

import java.util.Properties;

/**
 * @author yangcong
 * <p>
 * 服务注册
 */
public class RegisterService extends AbstractNacos {

    /**
     * 构建Nacos配置
     *
     * @param properties
     * @throws NacosException
     */
    public RegisterService(Properties properties) throws NacosException {
        super(properties);
    }

    /**
     * 服务注册
     *
     * @param instance
     * @return
     */
    public boolean serviceRegister(Instance instance) {
        try {
            super.getNamingService().registerInstance(instance.getServiceName(), instance);
        } catch (NacosException e) {
            //记录日志

            //出现异常后返回false
            return false;
        }
        return true;
    }
}
