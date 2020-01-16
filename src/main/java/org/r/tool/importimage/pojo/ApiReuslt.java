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
        ApiReuslt<T> result = new ApiReuslt<>();
        result.setCode("200");
        return result;
    }


}
