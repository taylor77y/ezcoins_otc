package com.ezcoins.project.consumer.entity.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/9 15:01
 * @Version:1.0
 */
@Data
public class EzUserReqDto {

    @ApiModelProperty(value = "用户编号")
    private String userId;

    @ApiModelProperty(value = "用户名",required = true)
    private String userName;

    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "{password.not}")
    private String password;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "电话国际区号")
    private String phoneArea;

    @ApiModelProperty(value = "国家编号",required = true)
    private String countryCode;

    @ApiModelProperty(value = "验证码",required = true)
    @NotBlank(message = "{code.not}")
    private String code;

    @ApiModelProperty(value = "验证方式  1：手机验证  2：邮箱验证",required = true)
    private String type;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
