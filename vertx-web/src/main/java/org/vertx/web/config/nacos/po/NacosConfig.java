package org.vertx.web.config.nacos.po;

import com.beust.jcommander.Parameter;

/**
 * @author yangcong
 * <p>
 * Nacos属性配置
 */
public class NacosConfig {

    @Parameter(names = {"-address"}, description = "nacos地址", required = true)
    private String address;

    @Parameter(names = {"-username"}, description = "用户名", required = true)
    private String username;

    @Parameter(names = {"-password"}, description = "密码", required = true)
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "NacosConfig{" +
                "address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
