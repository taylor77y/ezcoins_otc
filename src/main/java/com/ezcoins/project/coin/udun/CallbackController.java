package com.ezcoins.project.coin.udun;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
public class CallbackController {
    @Autowired
    BiPayClient biPayClient;
    private Logger logger = LoggerFactory.getLogger(CallbackController.class);
    @Autowired
    private BiPayService biPayService;

    /**
     * 处理币付网关回调信息，包括充币
     * @param timestamp
     * @param nonce
     * @param body
     * @param sign
     * @return
     * @throws Exception
     */
    @RequestMapping("/bipay/notify")
    public void tradeCallback(@RequestParam("timestamp")String timestamp,
                                @RequestParam("nonce")String nonce,
                                @RequestParam("body")String body,
                                @RequestParam("sign")String sign,
                                HttpServletResponse response) throws Exception {
        logger.info("timestamp:{},nonce:{},sign:{},body:{}",timestamp,nonce,sign,body);
        if(!HttpUtil.checkSign(biPayClient.getMerchantKey(),timestamp,nonce,body,sign)){
			ServletUtils.renderErrorString(response,"fail");
        }
        Trade trade = JSONObject.parseObject(body,Trade.class);
        logger.info("trade:{}",trade);
        //金额为最小单位，需要转换,包括amount和fee字段
        BigDecimal amount = trade.getAmount().divide(BigDecimal.TEN.pow(trade.getDecimals()),8, RoundingMode.DOWN);
        BigDecimal fee = trade.getFee().divide(BigDecimal.TEN.pow(trade.getDecimals()),8, RoundingMode.DOWN);
        logger.info("amount={},fee={}",amount.toPlainString(),fee.toPlainString());
        
        //TODO 业务处理
        if(trade.getTradeType() == 1){
            logger.info("=====收到充币通知======");
            logger.info("address:{},amount:{},mainCoinType:{},fee:{}",trade.getAddress(),trade.getAmount(),trade.getMainCoinType(),trade.getFee());
            
            boolean rs = false;
            try {
                rs = biPayService.rechargeCallback(trade);
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
            logger.info("=====收到提币处理通知=====");
            logger.info("address:{},amount:{},mainCoinType:{},businessId:{}",trade.getAddress(),trade.getAmount(),trade.getMainCoinType(),trade.getBusinessId());
            if(trade.getStatus() == 1){
                logger.info("审核通过，转账中");
                //TODO: 提币交易已发出，理提币订单状态，扣除提币资金
            }
            else if(trade.getStatus() == 2){
                logger.info("审核不通过");
                //TODO: 处理提币订单状态，订单号为 businessId
            }
            else if(trade.getStatus() == 3){
                logger.info("提币已到账");
                //TODO: 提币已到账，可以向提币用户发出通知
            }
            
            boolean rs = false;
            try {
                rs = biPayService.withdrawCallback(trade);
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
