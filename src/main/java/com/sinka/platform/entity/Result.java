package com.sinka.platform.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "统一接口响应")
public class Result<T> implements Serializable {

    @Schema(description = "业务状态码：1 成功，0 失败")
    private Integer code; //编码：1成功，0和其它数字为失败
    @Schema(description = "错误信息；成功时通常为空")
    private String msg; //错误信息
    @Schema(description = "响应数据；无数据时为空")
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 0;
        return result;
    }

}
