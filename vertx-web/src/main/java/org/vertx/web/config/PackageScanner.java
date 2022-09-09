package org.vertx.web.config;

import java.util.Map;

import org.springframework.context.support.AbstractApplicationContext;
import org.vertx.web.annotations.Controller;

/**
 * @author yangcong
 * 
 *         package包扫描
 */
public class PackageScanner {

    /**
     * Spring上下文
     */
    private AbstractApplicationContext abstractApplicationContext;

    /**
     * 控制层Beans
     */
    private Map<String, Object> controllerBeans;

    public PackageScanner(AbstractApplicationContext abstractApplicationContext) {
        this.abstractApplicationContext = abstractApplicationContext;
        this.controllerBeans = this.abstractApplicationContext.getBeansWithAnnotation(Controller.class);
    }

    public Map<String, Object> getControllerBeans() {
        return controllerBeans;
    }
}
