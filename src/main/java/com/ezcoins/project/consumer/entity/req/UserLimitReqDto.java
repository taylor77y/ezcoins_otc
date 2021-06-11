package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/10 10:34
 * @Version:1.0
 */
@Data
public class UserLimitReqDto {

    @ApiModelProperty(value = "用户id")
    @NotBlank(message = "{审核类型不能为空}")
    private String userId;

    @ApiModelProperty(value = "封号原因")
    private String detailed;

    @ApiModelProperty(value = "封禁类型( 登录封禁：1)")
    @NotBlank(message = "{封禁类型不能为空}")
    private String type;

    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    @NotBlank(message = "{操作类型不能为空}")
    private String operate;

}
