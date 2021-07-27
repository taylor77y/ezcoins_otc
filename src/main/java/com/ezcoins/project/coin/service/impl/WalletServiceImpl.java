package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.RecordSonType;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.coin.WithdrawOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.RechargeConfig;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.Wallet;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.mapper.WalletMapper;
import com.ezcoins.project.coin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.udun.Address;
import com.ezcoins.project.coin.udun.BiPayService;
import com.ezcoins.project.coin.udun.Trade;
import com.ezcoins.project.coin.wallet.cc.ChainCoinType;
import com.ezcoins.project.coin.wallet.cc.CoinTypeUtils;
import com.ezcoins.project.coin.wallet.cc.WalletClientService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
@Slf4j
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    @Autowired
    private RechargeConfigService configService;

    @Autowired
    private WalletClientService walletClientService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WithdrawOrderService withdrawOrderService;


    @Autowired
    private RechargeConfigService rechargeConfigService;

    @Override
    public Response rechargeAddress(String userId, String id) {
        String rechargeAddr = null;
        //根据id查询充值地址main_type  coin_type
        RechargeConfig rechargeConfig = configService.getById(id);
        String mainCoinType = rechargeConfig.getMainCoinType();
        //通过用户id 查询钱包列表
        LambdaQueryWrapper<Wallet> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Wallet::getUserId, userId);
        List<Wallet> wallets = baseMapper.selectList(lambdaQueryWrapper);

        String finalMainCoinType = mainCoinType;
        List<Wallet> collect = wallets.stream().filter(e -> e.getMainCoinType().equals(finalMainCoinType)).collect(Collectors.toList());
        if (collect.size() != 0) {
            rechargeAddr = collect.get(0).getAddress();
        } else if ("60".equals(mainCoinType)) {
            Address coinAddress = walletClientService.createAddressList("test");
            Wallet wallet = new Wallet();
            if (StringUtils.isNotNull(coinAddress)) {
                Integer integer = coinAddress.getId();
                if (integer == 3) {
                    mainCoinType = "60";
                    wallet.setAddress(coinAddress.getAddress());
                    wallet.setMainCoinType(mainCoinType);
                    wallet.setUserId(userId);
                    wallet.setWalletType("thirdparty");
                    baseMapper.insert(wallet);
                }
            }
            rechargeAddr = wallet.getAddress();
        } else {
//            Address coinAddress = biPayService.createCoinAddress(mainCoinType, userId.toString(), "");
//            Wallet wallet = new Wallet();
//            if (StringUtils.isNotNull(coinAddress) || true) {
//               wallet.setAddress(coinAddress.getAddress());
//                wallet.setAddress(mainCoinType + "/" + ContextHandler.getUserId());
//                wallet.setMainCoinType(mainCoinType);
//                wallet.setUserId(userId);
//                wallet.setWalletType("thirdparty");
//                baseMapper.insert(wallet);
//            }
            throw new BaseException("币种充值尚未开启");

        }
        HashMap map = new HashMap(1);
        map.put("rechargeAddr", rechargeAddr);
        return Response.success(map);
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
        WithdrawOrder withdrawOrder = null;
        if (StringUtils.isNotNull(trade) && StringUtils.isNotNull(trade.getRequest_id())) {
            //移除企业编号
            Long businessId = Long.parseLong(trade.getRequest_id().replace(CoinConstants.BUSINESSID, ""));
            withdrawOrder = withdrawOrderService.getById(businessId);
            if (StringUtils.isNull(withdrawOrder)) {
                return false;
            }
            cr = recordService.getById(withdrawOrder.getCoinRecordId());
        }
        if (StringUtils.isNull(cr)) {
            return false;
        }
        //判断是否为提款，不是就返回false
        if (trade.getTt() != 2) {
            return false;
        }
        // [审核通过]判断是否审核中 扣除冻结
        if (trade.getS() == 2 && CoinConstants.RecordStatus.WAIT.getStatus().equals(cr.getStatus())) {
            cr.setStatus(CoinConstants.RecordStatus.PASS.getStatus());
            List<BalanceChange> cList = new ArrayList<BalanceChange>();
            BalanceChange c = new BalanceChange();
            c.setFrozen(cr.getAmount().add(cr.getFee()).negate());
            c.setUserId(cr.getUserId());
            c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            c.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
            c.setSonType(RecordSonType.ORDINARY_WITHDRAWAL);
            cList.add(c);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
            withdrawOrder.setStatus(WithdrawOrderStatus.BYWALLET.getCode());
        }
        // [审核拒绝]判断是否审核中 解冻返回余额 返回Txid
        if (trade.getS() == 3 && CoinConstants.RecordStatus.WAIT.getStatus().equals(cr.getStatus())) {
            cr.setStatus(CoinConstants.RecordStatus.REFUSE.getStatus());
            cr.setTxid(trade.getTid());
            List<BalanceChange> cList = new ArrayList<BalanceChange>();
            BalanceChange c = new BalanceChange();
            c.setAvailable(cr.getAmount().add(cr.getFee()));
            c.setFrozen(cr.getAmount().negate());
            c.setUserId(cr.getUserId());
            c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            c.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
            c.setSonType(RecordSonType.ORDINARY_WITHDRAWAL);
            cList.add(c);
            if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                throw new AccountOperationBusyException();
            }
            withdrawOrder.setStatus(WithdrawOrderStatus.FAIL.getCode());
        }
        withdrawOrderService.updateById(withdrawOrder);
        recordService.updateById(cr);
        return true;
    }

    /**
     * 处理第三方提币审核结果 [审核通过][审核拒绝][到账返回Txid]
     *
     * @param txId
     * @param address
     * @param amount
     * @param mainCoinType
     * @param coinType
     */
    @Override
    public boolean handleRecharge(String txId, String address, BigDecimal amount, String mainCoinType, String coinType) {
        Record record = recordService.selectCoinRecordByTxId(txId); //通过交易id查询流水记录
        if (StringUtils.isNotNull(record)) {
            log.error("重复充值Txid[{}]！！！", txId);
            return true;
        }
        List<BalanceChange> cList = new ArrayList<BalanceChange>();
        BalanceChange c = new BalanceChange();
        c.setAvailable(amount); //设置余额


        LambdaQueryWrapper<RechargeConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RechargeConfig::getMainCoinType, mainCoinType);
        lambdaQueryWrapper.eq(RechargeConfig::getCoinType, coinType);
        RechargeConfig config = rechargeConfigService.getOne(lambdaQueryWrapper);
        if (StringUtils.isNull(config)) {
            log.error("币种mainCoinType{}，coinType：{}找不到！！！", mainCoinType, coinType);
            return false;
        }

        c.setCoinName(config.getCoinName()); //设置币种
        c.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
        c.setMainType(CoinConstants.MainType.RECHARGE.getType());
        c.setSonType(RecordSonType.ORDINARY_RECHARGE);

        LambdaQueryWrapper<Wallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Wallet::getAddress, address);
        queryWrapper.eq(Wallet::getMainCoinType, mainCoinType);
        Wallet wallet = baseMapper.selectOne(queryWrapper); //通过地址查询到钱包信息
        if (StringUtils.isNull(wallet)) {
            log.error("参数异常-找到不到充值地址[{}]", address);
            return false;
        }
        c.setUserId(wallet.getUserId());
        cList.add(c);

        boolean sync = false;
        try {
            sync = accountService.balanceChangeSYNC(cList);
        } catch (Exception e) {
            log.info("资产变更异常");
            return false;// 资产变更异常
        }
        if (!sync) {// 资产变更异常
            log.info("资产变更失败");
            return false;
        }

        Record rec = new Record(); //添加流水记录
        rec.setUserId(c.getUserId());
        rec.setCoinName(c.getCoinName());
        rec.setCreateTime(DateUtils.getNowDate());
        rec.setFee(BigDecimal.ZERO);
        rec.setIncomeType(c.getIncomeType());
        rec.setMainType(c.getMainType());
        rec.setSonType(c.getSonType());
        rec.setStatus(CoinConstants.RecordStatus.OK.getStatus());
        rec.setAmount(c.getAvailable());
        rec.setFromAddress(null);
        rec.setToAddress(address);
        rec.setTxid(txId);

        recordService.save(rec);
        return true;
    }


}
