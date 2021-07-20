package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.coin.WithdrawOrderStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.exception.user.ParameterException;
import com.ezcoins.exception.user.SystemBusyException;
import com.ezcoins.project.coin.entity.Record;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.ezcoins.project.coin.entity.WithdrawOrder;
import com.ezcoins.project.coin.entity.req.CheckWithdrewOrderReqDto;
import com.ezcoins.project.coin.entity.req.WithdrawReqDto;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.mapper.WithdrawOrderMapper;
import com.ezcoins.project.coin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.coin.udun.BiPayService;
import com.ezcoins.project.coin.udun.CoinType;
import com.ezcoins.project.coin.udun.ResponseMessage;
import com.ezcoins.project.coin.wallet.cc.ChainCoinType;
import com.ezcoins.project.coin.wallet.cc.CoinTypeUtils;
import com.ezcoins.project.coin.wallet.cc.WalletClientService;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.beans.beancontext.BeanContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
@Slf4j
public class WithdrawOrderServiceImpl extends ServiceImpl<WithdrawOrderMapper, WithdrawOrder> implements WithdrawOrderService {
    @Autowired
    private WithdrawConfigService withdrawConfigService;

    @Autowired
    private WalletClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RecordService recordService;

    /***
     * @Description: 审核提币 订单
     * @Param: [checkWithdrewOrderReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/17
     * @param checkWithdrewOrderReqDto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void reviewWithdrawOrder(CheckWithdrewOrderReqDto checkWithdrewOrderReqDto) {
        WithdrawOrder withdrawOrder = baseMapper.selectById(checkWithdrewOrderReqDto.getId());
        String userName = ContextHandler.getUserName();
        if (!withdrawOrder.getStatus().equals(WithdrawOrderStatus.PENDINGREVIEW.getCode())) {
            throw new BaseException("该提币订单审核失败");
        } else if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.BYADMIN.getCode())) {
            //TODO: 去优盾处理
            log.info("后台管理员 {}： 审核通过", userName);
            try {
                ChainCoinType chainCoinType = CoinTypeUtils.getChainCoinType(withdrawOrder.getMainCoinType(), withdrawOrder.getCoinType());
                if (chainCoinType == null) {
                    throw new BaseException("未获取到coinType");
                }
                Integer code = clientService.transfer(chainCoinType.getCoin_type(), chainCoinType.getChain(), withdrawOrder.getId(), withdrawOrder.getAddress(), "test", withdrawOrder.getAmount());
                if (code != 200) {
                    throw new SystemBusyException();
                }
                withdrawOrder.setStatus(WithdrawOrderStatus.BYADMIN.getCode());
            } catch (Exception e) {
                throw new ParameterException();
            }
        } else if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.REFUSE.getCode())) {
            //修改订单状态
            withdrawOrder.setStatus(WithdrawOrderStatus.REFUSE.getCode());
            withdrawOrder.setReason(checkWithdrewOrderReqDto.getReason());
            //解冻提币金额
            BigDecimal total = withdrawOrder.getAmount().add(withdrawOrder.getFee());
            List<BalanceChange> cList = new ArrayList<BalanceChange>();
            BalanceChange c = new BalanceChange();
            c.setAvailable(total);// 扣除金额包括 提现数量+固定额度+比例额度
            c.setFrozen(total.negate()); //设置冻结金额
            c.setCoinName(withdrawOrder.getCoinName());
            c.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            c.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            c.setUserId(withdrawOrder.getUserId());
            cList.add(c);

            String coinRecordId = withdrawOrder.getCoinRecordId();
            LambdaUpdateWrapper<Record> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Record::getId,coinRecordId);
            lambdaUpdateWrapper.set(Record::getStatus,CoinConstants.RecordStatus.REFUSE.getStatus());
            recordService.update(null,lambdaUpdateWrapper);

            log.info("后台管理员 {}： 审核失败,原因：{}", userName, checkWithdrewOrderReqDto.getReason());
        }
        withdrawOrder.setUpdateBy(userName);
        baseMapper.updateById(withdrawOrder);
    }

    /**
     * 发起提现
     *
     * @param withdrawReqDto
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseResponse withdraw(WithdrawReqDto withdrawReqDto) {
        String userId = ContextHandler.getUserId();

        LambdaQueryWrapper<WithdrawConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WithdrawConfig::getCoinName, withdrawReqDto.getCoinName());
        WithdrawConfig one = withdrawConfigService.getOne(lambdaQueryWrapper);
        if ("1".equals(one.getStatus())) {
            return BaseResponse.error(MessageUtils.message("该币种提币已关闭"));
        }
        BigDecimal amount = withdrawReqDto.getAmount();
        if (amount.compareTo(one.getMaxWithdraw()) > 0 && amount.compareTo(one.getMinWithdraw()) < 0) {
            return BaseResponse.error(MessageUtils.message("提币数量不在范围内"));
        }
        //计算手续费
        BigDecimal fee = amount.multiply(one.getFeeRate()).add(one.getFee());
        WithdrawOrder withdrawOrder = new WithdrawOrder();
        withdrawOrder.setAddress(withdrawReqDto.getToAddress());
        withdrawOrder.setAmount(amount);
        withdrawOrder.setCoinName(withdrawReqDto.getCoinName());
        withdrawOrder.setFee(fee);
        withdrawOrder.setCoinType(one.getCoinType());
        withdrawOrder.setUserId(ContextHandler.getUserId());
        withdrawOrder.setMainCoinType(one.getMainCoinType());
        withdrawOrder.setCreateBy(ContextHandler.getUserName());
        withdrawOrder.setStatus(WithdrawOrderStatus.PENDINGREVIEW.getCode());
        withdrawOrder.setUserId(userId);

        //冻结提取数量+手续费
        BigDecimal total = amount.add(fee);
        List<BalanceChange> cList = new ArrayList<BalanceChange>();
        BalanceChange c = new BalanceChange();
        c.setAvailable(total.negate());// 扣除金额包括 提现数量+固定额度+比例额度
        c.setFrozen(total); //设置冻结金额
        c.setCoinName(withdrawReqDto.getCoinName());
        c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        c.setMainType(CoinConstants.MainType.FROZEN.getType());
        c.setUserId(userId);
        cList.add(c);

        Record cr = new Record();
        // 插入记录
        cr.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        cr.setSonType("withdrawal");
        cr.setStatus(CoinConstants.RecordStatus.WAIT.getStatus());
        cr.setCoinName(withdrawOrder.getCoinName());
        cr.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
        cr.setAmount(total);
        cr.setFee(withdrawOrder.getFee());
        cr.setToAddress(withdrawReqDto.getToAddress());
        cr.setUserId(userId);

        recordService.save(cr);
        withdrawOrder.setCoinRecordId(cr.getId());
        baseMapper.insert(withdrawOrder);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        return BaseResponse.success();
    }
}
