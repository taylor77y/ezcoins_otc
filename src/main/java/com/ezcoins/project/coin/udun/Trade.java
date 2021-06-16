package com.ezcoins.project.coin.udun;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Trade {
    //交易Id
    private String txId ;
    //交易流水号
    private String tradeId ;
    //交易地址
    private String address ;
    //币种类型
    private String mainCoinType;
    //代币类型，erc20为合约地址
    private String coinType;
    //交易金额
    private BigDecimal amount ;
    //区块高度
    private String blockHigh;
    //交易类型  1-充值 2-提款(转账)
    private int tradeType ;
    //交易状态 0-待审核 1-成功 2-失败,充值无审核
    private int status ;
    //旷工费
    private BigDecimal fee ;
    private int decimals;
    //提币申请单号
    private String businessId ;
    //备注
    private String memo;

    /**
     * TRX代币-TRC20
     * @return
     */
    public boolean isTrcToken(){
        return "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t".equalsIgnoreCase(this.coinType) &&
                this.mainCoinType.equalsIgnoreCase(CoinType.TRX.getCode());
    }
    /**
     * ETH代币-ERC20
     * @return
     */
    public boolean isErcToken(){
        return "0xdac17f958d2ee523a2206206994597c13d831ec7".equalsIgnoreCase(this.coinType) &&
                this.mainCoinType.equalsIgnoreCase(CoinType.Ethereum.getCode());
    }
    public boolean isUsdt(){
        return this.mainCoinType.equalsIgnoreCase(CoinType.Bitcoin.getCode())
                && "31".equalsIgnoreCase(this.coinType);
    }
    public boolean isETH(){
        return this.mainCoinType.equalsIgnoreCase(this.coinType) &&
                this.mainCoinType.equalsIgnoreCase(CoinType.Ethereum.getCode());
    }
    /**
     * BTC
     * @return
     */
    public boolean isBTC(){
        return this.mainCoinType.equalsIgnoreCase(CoinType.Bitcoin.getCode())
                && "0".equalsIgnoreCase(this.coinType);
    }
    
    public static void main(String[] args) {
//    	trade:Trade(txId=a243c6e3190eb303c4515bd6b7c5d990572aa6c422ae5849f62c4c8d7a4390a9, tradeId=766345972027277312, 
//    	address=LM8PVmr4Hm6UAQ5bAv7df3EXGYBHX41fS9, mainCoinType=2, coinType=2, amount=10900000, blockHigh=1930335, 
//    	tradeType=1, status=3, fee=0, decimals=8, businessId=null, memo=)
    	Trade t = new Trade();
    	t.setTxId("ec35690efda5046afa43da0a289119d6302687536a28995f2d2ca66cb734741d");
    	t.setTradeId("770671872884654080");
    	t.setAddress("TU2evTxaeMm5HSRAz9QavuRys6ddC5Eeas");
    	t.setMainCoinType("195");
    	t.setCoinType("TH6MBGKfQRjnXBbEihXBSDZeLoyqUN7WYJ");
    	t.setAmount(new BigDecimal("70000"));
    	t.setBlockHigh("24493570");
    	t.setTradeType(1);
    	t.setStatus(3);
    	t.setFee(BigDecimal.ZERO);
    	t.setDecimals(18);
    	
    	String s = JSONObject.toJSONString(t);
    	System.out.println(s);
    	
    }
}
