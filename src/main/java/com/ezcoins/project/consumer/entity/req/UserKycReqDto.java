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

    @ApiModelProperty(value = "身份证",required = true)
    private String identityCard;

    @ApiModelProperty(value = "1身份证 2护照",required = true)
    @NotBlank(message = "{证件类型不能为空}")
    private String identityCardType;

    @ApiModelProperty(value = "前照",required = true)
    @NotBlank(message = "{前照不能为空}")
    private String frontPicture;

    @ApiModelProperty(value = "后照",required = true)
    @NotBlank(message = "{后照不能为空}")
    private String afterPicture;

    @ApiModelProperty(value = "居住证明,多张照片，用逗号隔开",required = true)
    private String livePicture;

    @ApiModelProperty(value = "手持自拍",required = true)
    @NotBlank(message = "{手持自拍照不能为空}")
    private String holdPicture;
}
