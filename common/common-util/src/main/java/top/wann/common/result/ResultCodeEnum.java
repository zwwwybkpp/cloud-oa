package top.wann.common.result;

import lombok.Getter;

/**
 * @author wannn
 * @date 2023/4/26 23:44
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务器异常"),
    DATA_ERROR(204, "数据异常"),
    //    LOGIN_AUTH(208, "未登录"),
    LOGIN_ERROR(207, "认证失败"),
    PERMISSION(209, "没有操作权限");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
