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
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "封号原因")
    @NotBlank(message = "请输入封号原因")
    private String detailed;

    @ApiModelProperty(value = "封禁类别（0：登录封禁 1：提现封禁 2：发布广告封禁 3：买卖封禁 ）")
    private String type;

    @ApiModelProperty(value = "封禁时间(天数)")
    private Integer day;


}
