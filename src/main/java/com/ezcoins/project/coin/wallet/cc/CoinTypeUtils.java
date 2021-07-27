package com.ezcoins.project.coin.wallet.cc;


import com.ezcoins.project.coin.udun.CoinType;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/7/15 15:10
 * @Version:1.0
 */
public class CoinTypeUtils {
    private CoinTypeUtils() {
    }
    public static ChainCoinType getChainCoinType(String mainCoinType,String coinType) {
        if (CoinType.Ethereum.getCode().equals(mainCoinType)) {
            if ("0xdac17f958d2ee523a2206206994597c13d831ec7".equals(coinType)) {
                return ChainCoinType.ERC20;
            } else if (CoinType.Ethereum.getCode().equals(coinType)) {
                return ChainCoinType.ETH;
            }
        } else if (CoinType.TRX.getCode().equals(mainCoinType)) {
            if ("TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t".equals(coinType)) {
                return ChainCoinType.TRC20;
            }
        } else if (CoinType.Bitcoin.getCode().equals(mainCoinType)) {
            if (CoinType.Bitcoin.getCode().equals(coinType)) {
                return ChainCoinType.BTC;
            }
        }
        return null;
    }
    public static String getMainCoinType(String chain,String coin){
        if (chain.equals(ChainCoinType.ERC20.getChain()) && coin.equals(ChainCoinType.ERC20.getCoin_type())){
            return CoinType.Ethereum.getCode();
        }
        return null;

    }
    public static String getCoinType(String chain,String coin){
        if (chain.equals(ChainCoinType.ERC20.getChain()) && coin.equals(ChainCoinType.ERC20.getCoin_type())){
            return "0xdac17f958d2ee523a2206206994597c13d831ec7";
        }
        return null;
    }





}
