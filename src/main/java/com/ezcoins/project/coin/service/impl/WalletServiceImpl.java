package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.RechargeConfig;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.Wallet;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.mapper.WalletMapper;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.coin.service.RechargeConfigService;
import com.ezcoins.project.coin.service.RecordService;
import com.ezcoins.project.coin.service.WalletService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.udun.Address;
import com.ezcoins.project.coin.udun.BiPayService;
import com.ezcoins.project.coin.udun.Trade;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * <p>
 * 钱包地址表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-25
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    @Autowired
    private RechargeConfigService configService;

    @Autowired
    private BiPayService biPayService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private AccountService accountService;

    @Override
    public BaseResponse rechargeAddress(String userId, String id) {
        String rechargeAddr = null;
        //根据id查询充值地址main_type  coin_type
        RechargeConfig rechargeConfig = configService.getById(id);
        String mainCoinType = rechargeConfig.getMainCoinType();
        //通过用户id 查询钱包列表
        LambdaQueryWrapper<Wallet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Wallet::getUserId, userId);
        List<Wallet> wallets = baseMapper.selectList(lambdaQueryWrapper);

        List<Wallet> collect = wallets.stream().filter(e -> e.getMainCoinType().equals(mainCoinType)).collect(Collectors.toList());
        if (collect.size()!=0){
            rechargeAddr=collect.get(0).getAddress();
        }else {
            Address coinAddress = biPayService.createCoinAddress(mainCoinType, userId.toString(), "");
            Wallet wallet = new Wallet();
            if (StringUtils.isNotNull(coinAddress) || true){
//                wallet.setAddress(coinAddress.getAddress());
                wallet.setAddress(mainCoinType+"/"+ContextHandler.getUserId());
                wallet.setMainCoinType(mainCoinType);
                wallet.setUserId(userId);
                wallet.setWalletType("thirdparty");
                baseMapper.insert(wallet);
            }
            rechargeAddr=wallet.getAddress();
        }
        return BaseResponse.success().data("rechargeAddr",rechargeAddr);
    }

    /**
     * 处理第三方提币审核结果 [审核通过][审核拒绝][到账返回Txid]
     *
     * @param trade
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean handleThirdpartyWithdrawal(Trade trade) {
        // 查询提币订单，没有就返回false
        Record cr = null;
        if (StringUtils.isNotNull(trade) && StringUtils.isNotNull(trade.getBusinessId())) {
            //移除企业编号
            Long businessId = Long.parseLong(trade.getBusinessId().replace(CoinConstants.BUSINESSID, ""));
            cr = recordService.getById(businessId);
        }
        if (StringUtils.isNull(cr)) {
            return false;
        }
        //判断是否为提款，不是就返回false
        if (trade.getTradeType() != 2) {
            return false;
        }

        // [审核通过]判断是否审核中 扣除冻结
        if (trade.getStatus() == 1 && CoinConstants.RecordStatus.PASS.getStatus().equals(cr.getStatus())) {
            cr.setStatus(CoinConstants.RecordStatus.OK.getStatus());
            List<BalanceChange> cList = new ArrayList<BalanceChange>();
            BalanceChange c = new BalanceChange();
            c.setFrozen(cr.getAmount().negate());
            c.setUserId(cr.getUserId());
            c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            c.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
            c.setSonType("withdrawal");
            cList.add(c);

            if (! accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        // [审核拒绝]判断是否审核中 解冻返回余额 返回Txid
        if (trade.getStatus() == 2 && CoinConstants.RecordStatus.PASS.getStatus().equals(cr.getStatus())) {
            cr.setStatus(CoinConstants.RecordStatus.REFUSE.getStatus());
            cr.setTxid(trade.getTxId());
            List<BalanceChange> cList = new ArrayList<BalanceChange>();
            BalanceChange c = new BalanceChange();
            c.setAvailable(cr.getAmount().add(cr.getFee()));
            c.setFrozen(cr.getAmount().negate());
            c.setUserId(cr.getUserId());
            c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            c.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
            c.setSonType("withdrawal");
            cList.add(c);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
        }
        // [到账 和区块高度]
        if (trade.getStatus() == 3) {
            cr.setStatus(CoinConstants.RecordStatus.OK.getStatus());
        }
        recordService.updateById(cr);
        return true;
    }
}
