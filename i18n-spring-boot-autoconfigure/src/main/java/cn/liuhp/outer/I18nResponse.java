package cn.liuhp.outer;

import cn.liuhp.core.EnumI18nInterface;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-15 19:21
 */
@Data
public class I18nResponse {

    private String code;

    private String msg;

    private Object data;


    /*
     *  默认中文msg
     * */
    public I18nResponse success(EnumI18nInterface responseEnum) {
        this.code = responseEnum.getCode();
        this.msg = responseEnum.getZh();
        return this;
    }


    /*
     *  默认中文msg
     * */
    public I18nResponse fail(EnumI18nInterface responseEnum) {
        this.code = responseEnum.getCode();
        this.msg = responseEnum.getZh();
        return this;
    }


}
