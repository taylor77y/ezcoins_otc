package com.ezcoins.project.consumer.entity.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机邮箱 效验体
 * @author Administrator
 */
@Data
public class PhoneCaptchaReqDto {
    /** 手机号**/
    @NotBlank(message ="{手机号不能为空}")
    @ApiModelProperty(value = "手机号")
    private String phonenumber;


    /** 验证码用于类型  1：注册 2：找回密码*/
    @ApiModelProperty(value = "验证码用于类型 1：注册 2：找回密码")
    private String captchaType;


    /**
     * 收支类型
     */
    public static enum PhoneCaptchaType {
        /** 注册 **/
        REGISTER("1"),

        /** 找回密码 **/
        RETRIEVE_PASSWORD("2");

        private final String type;

        private PhoneCaptchaType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return String.valueOf(type);
        }
    }
}

