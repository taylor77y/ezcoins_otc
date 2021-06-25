package com.ezcoins.project.coin.udun;


import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bipay")
public class RequestController {
    @Autowired
    private BiPayService biPayService;

//    /**
//     * 创建新地址
//     *
//     * @param coinType 币种类型
//     * @param alias 别名（用户ID）
//     * @return
//     */
//    @RequestMapping("/create-address")
//    public AjaxResult createCoinAddress(int coinType, String alias) {
//    	Address addr = biPayService.createCoinAddress(CoinType.codeOf(coinType),alias,"");
//        if(StringUtils.isNotNull(addr)) {
//        	return AjaxResult.success(addr);
//        }else {
//        	return AjaxResult.error("操作失败");
//        }
//    }
//
//    /**
//     * 发起转账请求
//     * @param orderId 提现订单ID
//     * @param mainCoinType 主币种类型
//     * @param subCoinType 子币种类型
//     * @param amount 数量
//     * @param address 接收地址
//     * @param memo 备注
//     * @return
//     */
//    @RequestMapping("/transfer")
//    public AjaxResult transfer(String orderId,int mainCoinType,String subCoinType, BigDecimal amount, String address, String memo) {
//        CoinType coin = CoinType.codeOf(mainCoinType);
//        ResponseMessage<String> resp = biPayService.transfer(orderId, amount, coin, subCoinType, address, memo);
//        if(resp.getCode()==200) {
//        	return AjaxResult.success(resp.getMessage(), resp.getData());
//        }else {
//        	return AjaxResult.error(resp.getCode(),resp.getMessage());
//        }
//
//    }
//
//    @RequestMapping("/test")
//    public AjaxResult test() {
//        return AjaxResult.success("Success");
//    }
//
//    /**
//     * 校验地址合法性
//     * @param mainCoinType 币种类型
//     * @param address 地址
//     * @return true合法   false不合法
//     */
//    @RequestMapping("/checkAddress")
//    public AjaxResult checkAddress(String mainCoinType, String address) throws Exception {
//        return AjaxResult.success(biPayService.checkAddress(mainCoinType, address));
//    }
//
//    /**
//     * 获取支持的币种
//     * @param showBalance 是否显示余额
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping("/getSupportCoin")
//    public AjaxResult getSupportCoin(Boolean showBalance) throws Exception {
//        return AjaxResult.success(biPayService.getSupportCoin(showBalance));
//    }
}
