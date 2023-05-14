package top.wann.common.result;

import lombok.Data;

/**
 * ClassName: Result
 * Package: top.wann.common.result
 * Description:
 *
 * @Author wann
 * @Create 2023-03-01 9:52
 * @Version 1.0
 */

@Data
public class Result<T> {
    private Integer code; // 状态码
    private String message; // 返回信息
    private T data; // 统一返回的结果数据

    /**
     * 封装返回数据
     *
     * @param body
     * @param resultCodeEnum
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();

        // 封装数据
        if (body != null) {
            result.setData(body);
        }

        // 状态码
        result.setCode(resultCodeEnum.getCode());
        //返回信息
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    // 构造私有化 外部不能new
    private Result() {
    }

    // 成功 空结果
    public static <T> Result<T> ok() {
        return build(null, ResultCodeEnum.SUCCESS);
    }

    /**
     * 成功 返回有数据的结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    // 失败
    public static <T> Result<T> fail() {
        return build(null, ResultCodeEnum.FAIL);
    }

    /**
     * 失败  返回有数据的结果
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg) {
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }
}
