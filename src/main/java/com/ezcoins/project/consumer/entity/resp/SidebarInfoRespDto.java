package com.ezcoins.project.consumer.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 18:18
 * @Version:1.0
 */
@Data
public class SidebarInfoRespDto {
    @ApiModelProperty(value = "UID")
    private String userId;

    @ApiModelProperty(value = "名字")
    private String firstName;

    @ApiModelProperty(value = "姓氏")
    private String lastName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "区号")
    private String phoneArea;

    @ApiModelProperty(value = "安全密码状态 0：已设置 1：未设置")
    private String isSetting;

    @ApiModelProperty(value = "分享地址")
    private String shareAddr;

    @ApiModelProperty(value = "国家中文名")
    private String countryName;

    @ApiModelProperty(value = "国旗地址")
    private String nationalFlagAddr;

    @ApiModelProperty(value = "国家编号")
    private String countryCode;

    @ApiModelProperty(value = "国家电话区号")
    private String countryTelCode;


    @ApiModelProperty(value = "0:待审核 1：通过  2：未通过 3：未认证")
    private String kycStatus;


}
