package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.ezcoins.project.otc.entity.EzOtcOrderPayment;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/8/11 16:35
 * @Version:1.0
 */
@Data
public class OneOrderRespDto {
    @ApiModelProperty(value = "支付方式")
    @TableField(exist = false)
    private EzOtcOrderPayment payment;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户OTC昵称")
    private String matchAdvertisingName;

    @ApiModelProperty(value = "手续费")
    private BigDecimal fee;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @ApiModelProperty(value = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    @ApiModelProperty(value = "数量")
    private BigDecimal amount;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "国际货币")
    private String currencyCode;

    @ApiModelProperty(value = "订单状态(1:接单已取消 2:待接单 3：已取消 4：等待支付 5：已支付 6：已完成 7:申诉)")
    private String status;

    @ApiModelProperty(value = "币种类型")
    private String coinName;

    @ApiModelProperty(value = "订单到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dueTime;

    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    @ApiModelProperty(value = "支付详情id")
    private String orderPaymentId;

}
