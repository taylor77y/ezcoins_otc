package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.Wallet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.udun.Trade;
import com.ezcoins.response.Response;

import java.math.BigDecimal;

/**
 * <p>
 * 钱包地址表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-25
 */
public interface WalletService extends IService<Wallet> {


    /**
     * 获取充值地址
     * @param userId
     * @param id
     * @return
     */
    Response rechargeAddress(String userId,String id);


    /**
     * 处理第三方提币审核结果 [审核通过][审核拒绝][到账返回Txid]
     */
    boolean handleThirdpartyWithdrawal(Trade trade);




    /**
     * 处理第三方冲币审核结果 [审核通过][审核拒绝][到账返回Txid]
     */
    boolean handleRecharge(String txId, String address,BigDecimal amount, String mainCoinType,String coinType);
}
