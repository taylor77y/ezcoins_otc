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
public class MatchStatusReqDto {
    @ApiModelProperty(value = "匹配订单号",required = true)
    private String orderMatchNo;

    @ApiModelProperty(value = "安全密码",required = true)
    @NotBlank(message = "{安全密码不能为空}")
    private String securityPassword;
}
