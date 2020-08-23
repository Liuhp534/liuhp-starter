package cn.liuhp;

import cn.liuhp.core.I18nEnumContext;
import cn.liuhp.interceptor.CustomLocaleChangeInterceptor;
import cn.liuhp.listener.I18nSpringListener;
import cn.liuhp.outer.I18nResponse;
import cn.liuhp.outer.VariableResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Locale;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-22 17:18
 */
@Configuration
@ConditionalOnWebApplication
@Order(Ordered.HIGHEST_PRECEDENCE)
public class I18nAutoConfiguration extends WebMvcConfigurerAdapter {

    /*
    *  配置国际化组件
    * */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        // 设置默认的国际化区域
        sessionLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return sessionLocaleResolver;
    }

    /*
    *  根据参数设置指定的国际化数据
    * */
    @Bean
    public CustomLocaleChangeInterceptor customLocaleChangeInterceptor() {
        return new CustomLocaleChangeInterceptor();
    }

    /*
    *  配置国际化拦截器
    * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.customLocaleChangeInterceptor()).addPathPatterns("/**");
    }

    /*
    *  初始化国际化枚举的监听器
    * */
    @Bean
    public I18nSpringListener i18nSpringListener() {
        return new I18nSpringListener();
    }



    @ControllerAdvice
    private class I18nResponseAdvice implements ResponseBodyAdvice {

        @Override
        public boolean supports(MethodParameter returnType, Class converterType) {
            if (returnType.hasMethodAnnotation(RequestMapping.class)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                      MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            if (null != body) {
                Locale locale = LocaleContextHolder.getLocale();
                String msg = "";
                if (body instanceof VariableResponse) {
                    VariableResponse variableResponse = (VariableResponse) body;
                    msg = I18nEnumContext.convertMsgWithArgs(variableResponse.getCode(), locale.getLanguage(), variableResponse.getVariableArgs());
                    variableResponse.setMsg(msg);
                    return variableResponse;
                } else if (body instanceof I18nResponse) {
                    I18nResponse i18nResponse = (I18nResponse) body;
                    if ("zh".equalsIgnoreCase(locale.getLanguage())) {
                        return body;
                    }
                    msg = I18nEnumContext.convertMsg(i18nResponse.getCode(), locale.getLanguage());
                    i18nResponse.setMsg(msg);
                    return i18nResponse;
                }
            }
            return body;
        }
    }







}
