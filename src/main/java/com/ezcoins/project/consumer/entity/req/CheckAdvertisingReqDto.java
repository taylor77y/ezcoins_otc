package com.ezcoins.project.consumer.entity.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/30 11:32
 * @Version:1.0
 */
@Data
public class CheckAdvertisingReqDto {
    @ApiModelProperty(value = "高级认证编号",required = true)
    @NotBlank(message = "{高级认证编号不能为空}")
    private String id;

    @ApiModelProperty(value = "操作 (1：成功 2：失败)",required = true)
    @NotBlank(message = "{操作类型不能为空}")
    private String operate;

    @ApiModelProperty(value = "备注")
    private String memo;

}
