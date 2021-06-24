package com.ezcoins.project.otc.entity.resp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ezcoins.project.otc.entity.EzPaymentBank;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/15 18:02
 * @Version:1.0
 */
@Data
public class PaymentMethodRespDto {
    @ApiModelProperty(value = "详情编号")
    private String id;

    @ApiModelProperty(value = "开户银行名")
    private String bankName;

    @ApiModelProperty(value = "卖家真实姓名")
    private String realName;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "付款二维码 银行没有二维码")
    private String paymentQrCode;

    @ApiModelProperty(value = "状态(0:已激活 1：未激活) 只能激活三种方式")
    private String status;

    @ApiModelProperty(value = "收款二维码类型id：1：银行  2：支付宝 3：微信")
    private Integer paymentMethodId;
}
