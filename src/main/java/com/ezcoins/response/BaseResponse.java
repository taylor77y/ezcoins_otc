package com.ezcoins.response;

import com.ezcoins.constant.interf.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * 操作消息提醒
 * 
 * 
 */
@Data
public class BaseResponse{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    private BaseResponse(){
    }

    //成功静态方法
    public static BaseResponse success() {
        BaseResponse r = new BaseResponse();
        r.setSuccess(true);
        r.setCode(HttpStatus.HTTP_RES_CODE_200);
        r.setMessage(HttpStatus.HTTP_RES_CODE_200_VALUE);
        return r;
    }

    //失败静态方法
    public static BaseResponse error() {
        BaseResponse r = new BaseResponse();
        r.setSuccess(false);
        r.setCode(HttpStatus.HTTP_RES_CODE_500);
        r.setMessage(HttpStatus.HTTP_RES_CODE_500_VALUE);
        return r;
    }

    //失败静态方法
    public static BaseResponse error(String message) {
        BaseResponse r = new BaseResponse();
        r.setSuccess(false);
        r.setCode(HttpStatus.HTTP_RES_CODE_500);
        r.setMessage(message);
        return r;
    }
    public static BaseResponse error(Integer code, String message) {
        BaseResponse r = new BaseResponse();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public BaseResponse success(Boolean success){
        this.setSuccess(success);
        return this;
    }



    public BaseResponse message(String message){
        this.setMessage(message);
        return this;
    }

    public BaseResponse code(Integer code){
        this.setCode(code);
        return this;
    }


    public BaseResponse data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

    public BaseResponse data(Map<String, Object> map){
        this.setData(map);
        return this;
    }

}
