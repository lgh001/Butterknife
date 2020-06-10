package cn.lgh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lgh
 * @date 2020/6/9
 */
@Target(ElementType.FIELD) //说明这是一个属性注解
@Retention(RetentionPolicy.CLASS) //生命周期是编译阶段，不会被加载到jvm
public @interface BindView {
    int value();
}
