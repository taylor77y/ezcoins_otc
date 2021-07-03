package com.ezcoins.response;

import com.ezcoins.constant.interf.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/8 18:37
 * @Version:1.0
 */
@Data
public class Response<T>{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private T data ;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    private Response(){
    }
    //成功静态方法
    public static <T> Response<T> success(T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.HTTP_RES_CODE_200);
        r.setMessage(HttpStatus.HTTP_RES_CODE_200_VALUE);
        r.data= t;
        return r;
    }

    //成功静态方法
    public static <T> Response<T> success(String msg, T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.HTTP_RES_CODE_200);
        r.setMessage(msg);
        r.data= t;
        return r;
    }


    //成功静态方法
    public static <T> Response<T> error(String message) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.HTTP_RES_CODE_500);
        r.setMessage(message);
        return r;
    }

    //成功静态方法
    public static <T> Response<T> error(String message,Integer code) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public Response<T> message(String message){
        this.setMessage(message);
        return this;
    }


    //成功静态方法
    public static <T> Response<T> error(String message,T t) {
        Response<T> r = new Response<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.HTTP_RES_CODE_500);
        r.setMessage(message);
        r.setData(t);
        return r;
    }

    public Response<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
