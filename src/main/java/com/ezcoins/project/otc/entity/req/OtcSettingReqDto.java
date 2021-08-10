package com.ezcoins.project.otc.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/19 11:22
 * @Version:1.0
 */
@Data
public class OtcSettingReqDto {
    @ApiModelProperty(value = "卖家昵称",required = true)
    @NotBlank(message = "{advertising.name.not}")
    private String advertisingName;


    @ApiModelProperty(value = "资金密码",required = true)
    @NotBlank(message = "{security.password.not}")
    private String securityPassword;
}
