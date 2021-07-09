package com.ezcoins.project.common.entity.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/9 10:46
 * @Version:1.0
 */
@Data
public class IndexRespDto {
    @ApiModelProperty(value = "总注册人数")
    private Long signUp;

    @ApiModelProperty(value = "当天注册人数")
    private Long signUpDay;

    @ApiModelProperty(value = "总实名认证人数")
    private Long signUpKcy;

    @ApiModelProperty(value = "总高级认证人数")
    private Long signUpAdvertising;

    @ApiModelProperty(value = "总成交单数")
    private Long totalOrderSuccess;

    @ApiModelProperty(value = "当天成交单数")
    private Long dayOrderSuccess;

    @ApiModelProperty(value = "当天交易额")
    private BigDecimal dayTransactionAmount;

    @ApiModelProperty(value = "总交易额")
    private BigDecimal totalTransactionAmount;

    @ApiModelProperty(value = "总投诉单数")
    private Long totalOrderComplaint;

    @ApiModelProperty(value = "未处理投诉")
    private Long untreatedComplaint;

    @ApiModelProperty(value = "总充值数量")
    private BigDecimal totalRecharge;

    @ApiModelProperty(value = "总提现数量")
    private BigDecimal totalWithdraw;

}
