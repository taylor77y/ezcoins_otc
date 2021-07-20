package com.ezcoins.project.otc.entity.req;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/19 14:54
 * @Version:1.0
 */
@Data
public class StatusReqDto {
    @ApiModelProperty(value = "匹配订单号",required = true)
    private String id;

    @ApiModelProperty(value = "关闭/打开  1：关闭 0：打开",required = true)
    private String status;
}
