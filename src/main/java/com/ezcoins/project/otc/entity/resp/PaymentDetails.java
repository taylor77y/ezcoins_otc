package com.ezcoins.project.otc.entity.resp;

import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/23 16:50
 * @Version:1.0
 */
@Data
public class PaymentDetails {
    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "卖家昵称")
    private String advertisingName;

    @ApiModelProperty(value = "订单到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    @ApiModelProperty(value = "待接单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pendingOrderTime;

    @ApiModelProperty(value = "收款方式")
    private List<EzPaymentMethod> paymentMethodList;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成)")
    private String status;

}
