package rbac.framework.interfaces;

import java.util.Set;

import rbac.framework.object.User;

/**
 * @author yangcong
 * 
 * 授权&鉴权接口
 */
public interface Auth {

    /**
     * 授权
     * @return
     */
    public User authorization(String username, String password);

    /**
     * 鉴权
     * @param pis 当前用户的权限标识符
     * @param roles 当前接口所需角色
     * @return
     */
    public boolean authentication(Set<rbac.framework.object.Permissions> pis, String[] roles);
}
