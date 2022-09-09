package rbac.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rbac.framework.interfaces.Auth;
import rbac.framework.object.User;
import rbac.framework.utils.MD5Utils;

/**
 * @author yangcong
 * 
 * 安全检查
 */
public class Security {

    /**
     * 鉴权&授权
     */
    private static Auth auth;

    /**
     * 存储用户信息md5加密
     */
    private static Map<String, Object> users = new ConcurrentHashMap<String, Object>();

    /**
     * 存储用户
     */
    private static Map<String, User> userMap = new ConcurrentHashMap<String, User>();

    /**
     * 设置鉴权接口
     * @param auth
     */
    public static void setAuth(Auth auth){
        Security.auth = auth;
    }

    /**
     * 获取鉴权对象
     * @return
     */
    public static Auth auth(){
        return auth;
    }

    /**
     *
     * @param username
     * @param password
     */
    public static void login(String username, String password){
        User user = auth.authorization(username, password);

        String md5 = MD5Utils.md5(username + password + user.getId());
    
        //用户存入内存
        Security.users.put(md5, null);

        //存储用户信息,获取用户信息可以采用token
        Security.userMap.put(user.getToken(), user);
    }

    /**
     * 获取当前用户信息
     * @param token
     * @return
     */
    public static User getUser(String token){
        return Security.userMap.get(token);
    }

    /**
     * 登出
     * 
     * @param token
     * @return
     */
    public static boolean loginOut(String token){
        User user = Security.getUser(token);
        Security.userMap.remove(token);
        users.remove(user.getUsername() + user.getPassword() + user.getId());
        return true;
    }

}
