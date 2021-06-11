package com.ezcoins.project.consumer.entity.body;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/5/20 13:59
 * @Version:1.0
 */
@Data
public class UpdateUserBody {

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;
    /**
     * 安全密码
     */
    @ApiModelProperty(value = "安全密码")
    private String securityPassword;

    /** 新密码**/
    @ApiModelProperty(value = "新密码")
    private String newPassword;
    /**
     * 手机/邮箱的验证码
     */
    @ApiModelProperty(value = "验证码")
    private String code;

    /**
     * 身份证
     */
    @ApiModelProperty(value = "身份证")
    private String identityCard;

    /**
     * 1身份证 2护照
     */
    @ApiModelProperty(value = "1身份证 2护照")
    private String identityCardType;

}
