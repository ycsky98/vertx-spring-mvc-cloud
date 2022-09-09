package org.vertx.web.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vertx.web.annotations.Controller;
import org.vertx.web.annotations.RequestMappping;
import org.vertx.web.config.nacos.po.NacosConfig;
import org.vertx.web.middleware.url.router.RouterUrl;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import rbac.framework.Security;
import rbac.framework.interfaces.Auth;

/**
 * @author yangcong
 * <p>
 * web配置类
 */
public class WebConfig {

    /**
     * 配置文件
     */
    private String[] configFile;

    /**
     * 包扫描
     */
    private PackageScanner packageScanner;

    /**
     * Spring上下文
     */
    private AbstractApplicationContext abstractApplicationContext;

    /**
     * 存储url对应的实体(url, 源对象,方法)
     */
    private Map<String, ControllerEntity> url = new ConcurrentHashMap<>();

    /**
     * 路由
     */
    private RouterUrl routerUrl;

    /**
     * 用于自定义快速扫包
     */
    private Reflections reflections;

    /**
     * Nacos启动参数配置
     */
    private NacosConfig nacosConfig;

    public WebConfig(Vertx vertx, NacosConfig nacosConfig, String... configFile) {
        //nacos配置
        this.nacosConfig = nacosConfig;
        //spring配置文件组
        this.configFile = configFile;
        // 用于拉取Spring配置文件
        this.abstractApplicationContext = new ClassPathXmlApplicationContext(this.configFile);
        // 开始配置bean
        this.abstractApplicationContext.start();

        // 启用RPC服务
        new RpcConfig(this.abstractApplicationContext, this.nacosConfig);

        try {
            // 找不到bean的情况
            Security.setAuth(this.abstractApplicationContext.getBean(Auth.class));
        } catch (Exception e) {
        }

        // 配置全局自定义扫描
        this.reflections = new Reflections("", Arrays.asList(
                new SubTypesScanner(false)// 允许getAllTypes获取所有Object的子类, 不设置为false则 getAllTypes 会报错.默认为true.
                , new MethodParameterNamesScanner()// 设置方法参数名称 扫描器,否则调用getConstructorParamNames 会报错
                , new MethodAnnotationsScanner() // 设置方法注解 扫描器, 否则getConstructorsAnnotatedWith,getMethodsAnnotatedWith
                // 会报错
                , new MemberUsageScanner() // 设置 member 扫描器,否则 getMethodUsage 会报错, 不推荐使用,有可能会报错 Caused by:
                // java.lang.ClassCastException: javassist.bytecode.InterfaceMethodrefInfo
                // cannot be cast to javassist.bytecode.MethodrefInfo
                , new TypeAnnotationsScanner()// 设置类注解 扫描器 ,否则 getTypesAnnotatedWith 会报错
        ));

        // 对于部分注解的包扫描
        this.packageScanner = new PackageScanner(this.abstractApplicationContext);
        // 配置URL和实体对象的关系
        this.configURLwithObj();
        // 构建路由
        this.routerUrl = new RouterUrl(vertx, this);
        // 构建配置与路由,调起配置
        this.routerUrl.pathConfig();
    }

    /**
     * 获取Spring上下文
     *
     * @return
     */
    public AbstractApplicationContext getSpringApplication() {
        return this.abstractApplicationContext;
    }

    /**
     * 配置URL和实体对象的关系
     */
    public void configURLwithObj() {
        Map<String, Object> controllerBeans = this.packageScanner.getControllerBeans();
        Collection<Object> collection = controllerBeans.values();
        Method[] methods = null;
        String url = "";
        String controllerName = "";
        String version = "";
        ControllerEntity o = null;
        for (Object object : collection) {
            // 获取包含RequestMapping所有的method
            methods = Arrays.stream(object.getClass().getDeclaredMethods())
                    .filter(med -> med.isAnnotationPresent(RequestMappping.class)).toArray(Method[]::new);
            for (int i = 0; i < methods.length; i++) {
                // 获取当前Controller名称
                controllerName = object.getClass().getAnnotation(Controller.class).name();
                if (StringUtils.isEmpty(controllerName)) {
                    controllerName = object.getClass().getSimpleName();
                }

                // 获取接口版本号
                version = methods[i].getAnnotation(RequestMappping.class).version();
                // url构建
                url = "/" + controllerName.toLowerCase() + "/" + version.toLowerCase();
                if (this.url.containsKey(url)) {
                    throw new RuntimeException(
                            "URL ===>>>>>>>>>>>>>>" + url + " Path is existing OR Controller Version is existing !!!");
                }
                o = new ControllerEntity();
                o.setMethod(methods[i]);
                o.setObject(object);
                // 装填对象
                this.url.put(url, o);
            }
        }
    }

    public Map<String, ControllerEntity> getUrl() {
        return url;
    }

    /**
     * 返回配置好的路由
     *
     * @return
     */
    public Router getRouter() {
        return this.routerUrl.getRouter();
    }

    /**
     * 获取自定义扫描
     *
     * @return
     */
    public Reflections gReflections() {
        return this.reflections;
    }

}
