package com.ezcoins.project.coin.wallet.cc;

import com.alibaba.fastjson.JSONObject;
import com.ezcoins.project.coin.udun.BiPayClient;
import com.ezcoins.project.coin.udun.BiPayService;
import com.ezcoins.project.coin.udun.HttpUtil;
import com.ezcoins.project.coin.udun.Trade;
import com.ezcoins.utils.ServletUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Administrator
 */
@Slf4j
@RestController
@Api(tags = "Admin-第三方回滚")
public class WalletCallbackController {
    @Autowired
    private WalletClientService walletClientService;
    /**
     * 处理币付网关回调信息，包括充币
     * @param timestamp
     * @param body
     * @param sign
     * @return
     * @throws Exception
     */
    @RequestMapping("/bipay/notify")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tim estamp", value = "时间戳", required = true),
            @ApiImplicitParam(name = "key", value = "钥匙", required = true),
            @ApiImplicitParam(name = "body", value = "请求json字符串", required = true),
            @ApiImplicitParam(name = "sign", value = "md5加密后的签名", required = true)
    })
    public void tradeCallback(@RequestParam("timestamp")  String timestamp,
                                @RequestParam("key")String key,
                                @RequestParam("body")String body,
                                @RequestParam("sign")String sign,
                                HttpServletResponse response) throws Exception {
        log.info("timestamp:{},key:{},sign:{},body:{}",timestamp,key,sign,body);
        if(!HttpUtil.checkSign(key,timestamp,null,body,sign)){
			ServletUtils.renderErrorString(response,"fail");
        }
        Trade trade = JSONObject.parseObject(body,Trade.class);
        log.info("trade:{}",trade);
        //金额为最小单位，需要转换,包括amount和fee字段
        BigDecimal amount = trade.getAmount();
        BigDecimal fee = trade.getFee();

        log.info("amount={},fee={}",amount.toPlainString(),fee.toPlainString());
        //TODO 业务处理
        if(trade.getTradeType() == 1){
            log.info("=====收到充币通知======");
            log.info("address:{},amount:{},mainCoinType:{},fee:{}",trade.getAddress(),trade.getAmount(),trade.getMainCoinType(),trade.getFee());
            boolean rs = false;
            try {
                rs = walletClientService.rechargeCallback(trade);
            } catch (Exception e) {
                rs = false;
            }
            if (rs) {
				ServletUtils.renderString(response,"success");
			}else {
				ServletUtils.renderErrorString(response,"fail");
			}
            return;
        }
        else if(trade.getTradeType() == 2){
            log.info("=====收到提币处理通知=====");
            log.info("address:{},amount:{},mainCoinType:{},businessId:{}",trade.getAddress(),trade.getAmount(),trade.getMainCoinType(),trade.getRequest_id());
            if(trade.getStatus() == 1){
                log.info("审核通过，转账中");
                //TODO: 提币交易已发出，理提币订单状态，扣除提币资金
            }
            else if(trade.getStatus() == 2){
                log.info("审核不通过");
                //TODO: 处理提币订单状态，订单号为 businessId
            }
            else if(trade.getStatus() == 3){
                log.info("提币已到账");
                //TODO: 提币已到账，可以向提币用户发出通知
            }

            boolean rs = false;
            try {
                rs = walletClientService.withdrawCallback(trade);
            } catch (Exception e) {
                rs = false;
            }
            if (rs) {
				ServletUtils.renderString(response,"success");
			}else {
				ServletUtils.renderErrorString(response,"fail");
			}
            return;
        }
        response.setStatus(200);
		ServletUtils.renderString(response,"success");
    }
}
