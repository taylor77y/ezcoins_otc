package com.ezcoins.project.consumer.entity.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 手机 邮箱 验证码
 * @author Administrator
 */
@Data
public class VerificationCodeReqDto {
    @NotBlank(message ="{验证方式为空}")
    @ApiModelProperty(value = "验证方式  1：手机验证  2：邮箱验证",required = true)
    private String type;


    @NotBlank(message ="{号码不能为空}")
    @ApiModelProperty(value = "手机号/邮箱" ,required = true)
    private String verificationNumber;


    @ApiModelProperty(value = "手机国际区号")
    private String phoneArea;


    /** 验证码用于类型  1：注册 2：找回密码*/
    @ApiModelProperty(value = "验证码用于类型 1：注册 2：找回密码",required = true)
    @NotBlank(message ="{验证码用于类型不能为空}")
    private String captchaType;

    /**
     * 验证码用于类型
     */
    public static enum Type {
        /** 手机验证 **/
        PHONE("1"),

        /** 邮箱验证 **/
        EMAIL("2");

        private final String type;

        private Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return String.valueOf(type);
        }
    }



    /**
     * 验证码用于类型
     */
    public static enum captchaType {
        /** 注册 **/
        REGISTER("1"),

        /** 找回密码 **/
        RETRIEVE_PASSWORD("2"),

        /** 绑定信息 **/
        BIND_INFO("3");

        private final String type;

        private captchaType(String type) {
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

