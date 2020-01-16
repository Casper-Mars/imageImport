package org.r.tool.importimage.pojo;

import lombok.Data;

/**
 * @author casper
 * @date 20-1-16 下午1:37
 **/
@Data
public class ApiReuslt<T> {

    /**
     * 状态代码
     */
    private String code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;


    public static <T> ApiReuslt<T> success() {
        return success(null);
    }

    public static <T> ApiReuslt<T> success(T data) {
        ApiReuslt<T> result = new ApiReuslt<>();
        result.setCode("200");
        result.setData(data);
        return result;
    }


    public static <T> ApiReuslt<T> error(String msg) {
        ApiReuslt<T> result = new ApiReuslt<>();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }


}
