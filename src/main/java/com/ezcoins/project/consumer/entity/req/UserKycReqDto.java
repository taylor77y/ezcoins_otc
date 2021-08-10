package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author:
 * @Email:
 * @Description: 用户验证实体
 * @Date:2021/5/27 17:14
 * @Version:1.0
 */
@Data
public class UserKycReqDto {
    @ApiModelProperty(value = "用户id",required = false)
    private String userId;

    @ApiModelProperty(value = "名字",required = true)
    @NotBlank(message = "{firstName.not}")
    private String firstName;

    @ApiModelProperty(value = "姓氏",required = true)
    @NotBlank(message = "{lastName.not}")
    private String lastName;

    @ApiModelProperty(value = "国家编号",required = true)
    private String countryCode;

    @ApiModelProperty(value = "国家电话区号",required = true)
    private String countryTelCode;

    @ApiModelProperty(value = "当前居住国家",required = true)
    @NotBlank(message = "{addrCountry.not}")
    private String addrCountry;

    @ApiModelProperty(value = "当前居住城市",required = true)
    @NotBlank(message = "{addrCity.not}")
    private String addrCity;

    @ApiModelProperty(value = "邮政编码",required = true)
    @NotBlank(message = "{postalCode.not}")
    private String postalCode;

    @ApiModelProperty(value = "联系号码",required = true)
    @NotBlank(message = "{contactNumber.not}")
    private String contactNumber;

    @ApiModelProperty(value = "证件号码",required = true)
    @NotBlank(message = "{identityCard.not}")
    private String identityCard;

    @ApiModelProperty(value = "1身份证 2护照",required = true)
    private String identityCardType;

    @ApiModelProperty(value = "证件封面",required = true)
    @NotBlank(message = "{frontPicture.not}")
    private String frontPicture;

    @ApiModelProperty(value = "证件背面",required = true)
    @NotBlank(message = "{afterPicture.not}")
    private String afterPicture;

    @ApiModelProperty(value = "手持证件",required = true)
    @NotBlank(message = "{holdPicture.not}")
    private String holdPicture;

}
