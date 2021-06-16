package com.ezcoins.project.otc.entity.resp;

import com.ezcoins.project.otc.entity.EzUserAlipay;
import com.ezcoins.project.otc.entity.EzUserBank;
import com.ezcoins.project.otc.entity.EzUserWechat;
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
    @ApiModelProperty(value = "支付宝收款方式信息")
    private EzUserAlipay ezUserAlipay;

    @ApiModelProperty(value = "银行收款方式信息")
    private EzUserBank ezUserBank;

    @ApiModelProperty(value = "微信收款方式信息")
    private EzUserWechat ezUserWechat;
}
