package com.ezcoins.project.otc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wanglei
 * @since 2021-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="EzOtcOrderPayment对象", description="")
public class EzOtcOrderPayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "匹配订单号")
    private String orderMatchNo;

    @ApiModelProperty(value = "发布订单号")
    private String orderNo;

    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "卖家姓名")
    private String realName;

    @ApiModelProperty(value = "付款二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "收款二维码类型：1:银行 2：支付宝 3：微信")
    private Integer paymentMethodId;

    @ApiModelProperty(value = " 0：发布订单  1：匹配订单")
    private String type;

}
