package com.ezcoins.project.otc.entity.resp;

import com.ezcoins.project.coin.entity.Account;
import com.ezcoins.project.otc.entity.EzPaymentInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/20 15:36
 * @Version:1.0
 */
@Data
public class AdOrderRespDto {
    @ApiModelProperty(value = "币种类型列表")
    private List<String> coinNameList;

    @ApiModelProperty(value = "支付方式列表")
    private List<EzPaymentInfo> paymentInfos;

    @ApiModelProperty(value = "国际货币列表")
    private List<String> currencyCodeList;

    @ApiModelProperty(value = "可用余额")
    private List<Account> accounts;
}
