package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/22 11:14
 * @Version:1.0
 */
@Data
public class OtcOrderRespDto {
    @ApiModelProperty
    private String userId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "总成交单数")
    private Integer totalCount;

    @ApiModelProperty(value = "30天总完成率")
    private Double mouthFinishRate;

    @ApiModelProperty(value = "支付方式")
    private List<String> icons;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "剩余数量")
    private BigDecimal lastAmount;

    @ApiModelProperty(value = "最小限额")
    private BigDecimal minimumLimit;

    @ApiModelProperty(value = "最大限额")
    private BigDecimal maximumLimit;

}
