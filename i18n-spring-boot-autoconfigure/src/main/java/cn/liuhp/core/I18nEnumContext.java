package cn.liuhp.core;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-15 22:03
 */
public final class I18nEnumContext {

    /*
    *  国际化数据容器
    * */
    private static volatile Map<String, EnumI18nInterface>  i18nMap ;


    public static String convertMsg(String code, String locale) {
        EnumI18nInterface enumInterface = i18nMap.get(code);
        if ("zh".equalsIgnoreCase(locale)) {
            return enumInterface.getZh();
        } else {
            return enumInterface.getEn();
        }
    }

    public static String convertMsgWithArgs(String code, String locale, Object ... args) {
        EnumI18nInterface enumInterface = i18nMap.get(code);
        if ("zh".equalsIgnoreCase(locale)) {
            return args == null ? enumInterface.getZh() : MessageFormat.format(enumInterface.getZh(), args);
        } else {
            return args == null ? enumInterface.getEn() : MessageFormat.format(enumInterface.getEn(), args);
        }
    }

    /*
    *  配置国际化数据
    * */
    public static void configEnumI18n(List<EnumI18nInterface> enums) {
        if (null == enums) {
            return;
        }
        for (EnumI18nInterface enumInterface : enums) {
            getI18nMap().put(enumInterface.getCode(), enumInterface);
        }
    }

    /*
    *  获取容器
    * */
    private static Map<String, EnumI18nInterface> getI18nMap() {
        if (null == i18nMap) {
            synchronized (I18nEnumContext.class) {
                if (null == i18nMap) {
                    i18nMap = new ConcurrentHashMap<>(256);
                }
            }
        }
        return i18nMap;
    }

}
