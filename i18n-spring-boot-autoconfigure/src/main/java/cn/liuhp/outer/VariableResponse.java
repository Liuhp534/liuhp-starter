package cn.liuhp.outer;

import cn.liuhp.core.EnumI18nInterface;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @description:
 * @author: liuhp534
 * @create: 2020-08-23 10:51
 */
@Data
public class VariableResponse extends I18nResponse {

    /*
     *  占位符参数，不参数序列化
     * */
    @JsonIgnore
    private Object[] variableArgs;

    /*
     *  默认中文msg
     * */
    public VariableResponse success(EnumI18nInterface responseEnum, Object[] variableArgs) {
        super.success(responseEnum);
        this.variableArgs = variableArgs;
        return this;
    }

    /*
     *  默认中文msg
     * */
    public VariableResponse fail(EnumI18nInterface responseEnum, Object[] variableArgs) {
        super.fail(responseEnum);
        this.variableArgs = variableArgs;
        return this;
    }
}
