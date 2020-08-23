package cn.liuhp.interceptor;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-15 16:18
 */

public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String newLocale = request.getParameter(getParamName());
        if (newLocale != null) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException(
                        "No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(request, response, parseLocaleValue(newLocale));
            } catch (IllegalArgumentException ex) {
                if (isIgnoreInvalidLocale()) {
                    logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + ex.getMessage());
                } else {
                    throw ex;
                }
            }
        }

        return Boolean.TRUE;
    }
}
