package top.wann.common.config.exception;

import lombok.Data;
import top.wann.common.result.ResultCodeEnum;

/**
 * ClassName: GuiguException
 * Package: top.wann.common.config.exception
 * Description:
 *
 * @Author wann
 * @Create 2023-03-01 15:59
 * @Version 1.0
 */
@Data
public class GuiguException extends RuntimeException {

    private Integer code;

    private String msg;

    /**
     * 通过状态码和错误消息创建异常对象
     *
     * @param code
     * @param msg
     */
    public GuiguException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 接收枚举类型对象
     *
     * @param resultCodeEnum
     */
    public GuiguException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "GuiguException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

