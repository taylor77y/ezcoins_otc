package com.ezcoins.project.system.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/28 18:35
 * @Version:1.0
 */
@Data
public class SysTipsReqDto {
    @ApiModelProperty(value = "用户id  null：每个用户 不为null:单个用户")
    private String userId;

    @ApiModelProperty(value = "标题")
    @NotBlank(message = "请输入标题")
    private String title;

    @ApiModelProperty(value = "内容")
    @NotBlank(message = "请输入内容")
    private String content;
}
