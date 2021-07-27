package com.ezcoins.project.coin.wallet.cc;

public enum ChainCoinType {
    ERC20("1","1"),
    TRC20("2","1"),
    BTC("0","0"),
    ETH("0","0");


    private String chain;
    private String coin_type;

    ChainCoinType(String chain,String coin_type){
        this.chain=chain;
        this.coin_type=coin_type;
    }
    public String getChain(){
        return this.chain;
    }

    public String getCoin_type(){
        return this.coin_type;
    }
}
