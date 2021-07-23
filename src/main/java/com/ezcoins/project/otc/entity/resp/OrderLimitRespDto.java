package com.ezcoins.project.otc.entity.resp;

import com.ezcoins.project.coin.entity.Type;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/22 11:00
 * @Version:1.0
 */
@Data
public class OrderLimitRespDto {
    @ApiModelProperty(value = "付款最大期限（分钟）")
    private Integer maxPayTime;

    @ApiModelProperty(value = "付款最小期限（分钟）")
    private Integer minPayTime;

    @ApiModelProperty(value = "币种限制集合")
    List<Type> list;
}
