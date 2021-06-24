package com.ezcoins.response;

import com.ezcoins.constant.interf.HttpStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/8 18:37
 * @Version:1.0
 */
@Data
public class ResponseList<T> {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回码")
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "返回数据")
    private List<T> data ;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    private ResponseList(){
    }

    //成功静态方法
    public static <T> ResponseList<T> success(List<T> t) {
        ResponseList<T> r = new ResponseList<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.HTTP_RES_CODE_200);
        r.setMessage(HttpStatus.HTTP_RES_CODE_200_VALUE);
        r.data= t;
        return r;
    }

    //成功静态方法
    public static <T> ResponseList<T> error(String message,List<T> t) {
        ResponseList<T> r = new ResponseList<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.HTTP_RES_CODE_500);
        r.setMessage(HttpStatus.HTTP_RES_CODE_500_VALUE);
        if (t!=null){
            r.data=t;
        }
        return r;
    }


}
