package cn.liuhp.annotation;

import cn.liuhp.I18nAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
* 引入国际化功能
* */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(I18nAutoConfiguration.class)
public @interface EnableI18n {


    String i18nEnumPackage() default "cn.liuhp.**";




}
