package org.rpc.utils;

/**
 * @author yangcong
 * SpringBean名称转换类
 */
public class BeanNameFormat {
    
    public static String beanFormat(String beanName){
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1, beanName.length());
    }
}
