package org.vertx.web.middleware.auth;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.http.HttpServerRequest;
import rbac.framework.Security;
import rbac.framework.exceptions.UserLoginException;

/**
 * @author yangcong
 * Auth鉴权
 */
public class AuthUrlHandler {

    private HttpServerRequest httpServerRequest;

    /**
     * @param request
     * @param roles   当前接口的roles
     */
    public AuthUrlHandler(HttpServerRequest request, String[] roles) {
        this.httpServerRequest = request;

        // 检测token是否为空
        if (this.isBlanktoken(this.getToken())) {
            throw new RuntimeException("Token not find error");
        }

        rbac.framework.object.User user = Security.getUser(this.getToken());
        // 检测用户是否为空
        if (Objects.isNull(user)) {// 如果用户是空
            throw new UserLoginException();
        }
        // 获取当前用户角色权限标识符
        Set<rbac.framework.object.Permissions> pis = user.getRole().getPermissions();
        // 进行鉴权(具体实现在Auth实现类里面做)@param pis当前用户权限标识符 @param roles当前接口所需角色
        if (!Security.auth().authentication(pis, roles)) {// 无权限抛出异常
            throw new RuntimeException("No auth");
        }
    }

    /**
     * 获取token
     */
    public String getToken() {
        return this.httpServerRequest.getHeader("token");
    }

    public boolean isBlanktoken(String token) {
        return StringUtils.isEmpty(token);
    }
}
