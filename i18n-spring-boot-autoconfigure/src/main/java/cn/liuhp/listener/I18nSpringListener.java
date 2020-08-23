package cn.liuhp.listener;

import cn.liuhp.annotation.EnableI18n;
import cn.liuhp.core.EnumI18nInterface;
import cn.liuhp.core.I18nEnumContext;
import cn.liuhp.util.ClassScannerUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-15 22:01
 */
public class I18nSpringListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private String defaultPackageStr = "cn.liuhp.**";


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String packageStr = this.getPackageStr();
        if (StringUtils.isEmpty(packageStr)) {
            packageStr = defaultPackageStr;
        }
        List<EnumI18nInterface> allEnums = null;
        try {
            allEnums = ClassScannerUtils.analysisPackage(packageStr);
            if (null != allEnums) {
                I18nEnumContext.configEnumI18n(allEnums);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPackageStr() {
        String packageStr = null;
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        if (!CollectionUtils.isEmpty(beans)) {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                if (entry.getValue().getClass().isAnnotationPresent(EnableI18n.class)) {
                    EnableI18n enableI18n = entry.getValue().getClass().getAnnotation(EnableI18n.class);
                    packageStr = enableI18n.i18nEnumPackage();
                    break;
                } else if (entry.getValue().getClass().getSuperclass().isAnnotationPresent(EnableI18n.class)) {
                    EnableI18n enableI18n = entry.getValue().getClass().getSuperclass().getAnnotation(EnableI18n.class);
                    packageStr = enableI18n.i18nEnumPackage();
                    break;
                }
            }
        }
        return packageStr;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
