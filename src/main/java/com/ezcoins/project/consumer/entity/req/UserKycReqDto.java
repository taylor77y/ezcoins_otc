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
    @ApiModelProperty(value = "姓名",required = true)
    @NotBlank(message = "{姓名不能为空}")
    private String realName;

    @ApiModelProperty(value = "用户id",required = false)
    private String userId;

    @ApiModelProperty(value = "证件号码",required = true)
    @NotBlank(message = "{证件号码不能为空}")
    private String identityCard;

    @ApiModelProperty(value = "1身份证 2护照",required = true)
    private String identityCardType;


    @ApiModelProperty(value = "证件封面",required = true)
    @NotBlank(message = "{请上传证件封面}")
    private String frontPicture;

    @ApiModelProperty(value = "证件背面",required = true)
    @NotBlank(message = "{请上传证件背面}")
    private String afterPicture;

    @ApiModelProperty(value = "手持证件",required = true)
    @NotBlank(message = "{请上传手持证件照}")
    private String holdPicture;

    @ApiModelProperty(value = "紧急联系人电话",required = true)
    @NotBlank(message = "{紧急联系人电话不能为空}")
    private String urgentPhone;
}
