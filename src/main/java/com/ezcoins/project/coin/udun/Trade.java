package com.ezcoins.project.coin.udun;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Trade {
    private String um;

    //交易Id
//    private String txId ;
    private String tid ;// transaction hash
    //交易流水号
     private String tradeId ;
    //交易地址
//    private String address ;
    private String addr ;
    //币种类型
   // private String mainCoinType;
    private String chain;// 1 for ERC20, 2 for TRC20
    //代币类型，erc20为合约地址
   // private String coinType;
    private String coin;//1 For USDT
    //交易金额
//    private BigDecimal amount ;
    private BigDecimal amt ;//recharge amount
    //交易类型  1-充值 2-提款(转账)
//    private int tradeType ;
    private int tt ;// transaction type: 1 for recharge, 2 for withdrawal
    //交易状态 0-待审核 1-成功 2-失败,充值无审核
//    private int status ;
    private int s ;// status: 1 for pending, 2 for success, 3 for reject
    //旷工费
    private BigDecimal fee ;// gas fee
    //提币申请单号
    private String request_id ;




//    /**
//     * TRX代币-TRC20
//     * @return
//     */
//    public boolean isTrcToken(){
//        return "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t".equalsIgnoreCase(this.coinType) &&
//                this.mainCoinType.equalsIgnoreCase(CoinType.TRX.getCode());
//    }
//    /**
//     * ETH代币-ERC20
//     * @return
//     */
//    public boolean isErcToken(){
//        return "0xdac17f958d2ee523a2206206994597c13d831ec7".equalsIgnoreCase(this.coinType) &&
//                this.mainCoinType.equalsIgnoreCase(CoinType.Ethereum.getCode());
//    }
//    public boolean isUsdt(){
//        return this.mainCoinType.equalsIgnoreCase(CoinType.Bitcoin.getCode())
//                && "31".equalsIgnoreCase(this.coinType);
//    }
//    public boolean isETH(){
//        return this.mainCoinType.equalsIgnoreCase(this.coinType) &&
//                this.mainCoinType.equalsIgnoreCase(CoinType.Ethereum.getCode());
//    }
//    /**
//     * BTC
//     * @return
//     */
//    public boolean isBTC(){
//        return this.mainCoinType.equalsIgnoreCase(CoinType.Bitcoin.getCode())
//                && "0".equalsIgnoreCase(this.coinType);
//    }
//
//    public static void main(String[] args) {
////    	trade:Trade(txId=a243c6e3190eb303c4515bd6b7c5d990572aa6c422ae5849f62c4c8d7a4390a9, tradeId=766345972027277312,
////    	address=LM8PVmr4Hm6UAQ5bAv7df3EXGYBHX41fS9, mainCoinType=2, coinType=2, amount=10900000, blockHigh=1930335,
////    	tradeType=1, status=3, fee=0, decimals=8, businessId=null, memo=)
//    	Trade t = new Trade();
//    	t.setTxId("ec35690efda5046afa43da0a289119d6302687536a28995f2d2ca66cb734741d");
//    	t.setTradeId("770671872884654080");
//    	t.setAddress("TU2evTxaeMm5HSRAz9QavuRys6ddC5Eeas");
//    	t.setMainCoinType("195");
//    	t.setCoinType("TH6MBGKfQRjnXBbEihXBSDZeLoyqUN7WYJ");
//    	t.setAmount(new BigDecimal("70000"));
//    	t.setTradeType(1);
//    	t.setStatus(3);
//    	t.setFee(BigDecimal.ZERO);
//
//    	String s = JSONObject.toJSONString(t);
//    	System.out.println(s);
//
//    }
}
