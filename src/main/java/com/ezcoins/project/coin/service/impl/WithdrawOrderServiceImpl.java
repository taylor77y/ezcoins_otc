package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private BiPayService biPayService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TypeService typeService;

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
    public void reviewWithdrawOrder(CheckWithdrewOrderReqDto checkWithdrewOrderReqDto) {
        WithdrawOrder withdrawOrder = baseMapper.selectById(checkWithdrewOrderReqDto.getId());
        String userName = ContextHandler.getUserName();
        if (!withdrawOrder.getStatus().equals(WithdrawOrderStatus.PENDINGREVIEW.getCode())) {

            throw new BaseException("该提币订单审核失败");
        } else if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.BY.getCode())) {
            //TODO: 去优盾处理
            log.info("后台管理员 {}： 审核通过", userName);
            try {
                boolean checkAddress = biPayService.checkAddress(withdrawOrder.getMainCoinType(), withdrawOrder.getAddress());
                if (!checkAddress) {
                    withdrawOrder.setStatus(WithdrawOrderStatus.FAIL.getCode());
                    throw new BaseException("地址错误");
                }
                List<BalanceChange> cList = new ArrayList<BalanceChange>();

                BigDecimal total = withdrawOrder.getAmount().add(withdrawOrder.getFee());

               LambdaQueryWrapper<Type> queryWrapper=new LambdaQueryWrapper<>();
               queryWrapper.eq(Type::getCoinName,withdrawOrder.getCoinName());
                Type one = typeService.getOne(queryWrapper);

                BalanceChange c = new BalanceChange();
                c.setAvailable(total.negate());// 扣除金额包括 提现数量+固定额度+比例额度
                c.setFrozen(total); //设置冻结金额
                c.setCoinName(withdrawOrder.getCoinName());
                c.setCoinId(one.getId());
                c.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
                c.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
                c.setSonType("withdrawal");
                cList.add(c);
                if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
                    throw new AccountOperationBusyException();
                }

                Record cr=new Record();
                // 插入记录
                cr.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
                cr.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());

                cr.setSonType("withdrawal");
                cr.setStatus(CoinConstants.RecordStatus.PASS.getStatus());
                cr.setCoinName(withdrawOrder.getCoinName());
                cr.setCoinId(one.getId());
                cr.setMainType(CoinConstants.MainType.WITHDRAWAL.getType());
                cr.setAmount(total);
                cr.setFee(withdrawOrder.getFee());
                recordService.save(cr);

                CoinType coin = CoinType.codeOf(Integer.parseInt(withdrawOrder.getMainCoinType()));
                ResponseMessage<String> resp =
                        biPayService.transfer(
                                CoinConstants.BUSINESSID + cr.getId(),withdrawOrder.getAmount(),
                                coin, withdrawOrder.getCoinType(), withdrawOrder.getAddress(), "");
                if (resp.getCode() != 200) {
                    throw new SystemBusyException();
                }
            } catch (Exception e) {
                throw new ParameterException();
            }

        } else if (checkWithdrewOrderReqDto.getOperate().equals(WithdrawOrderStatus.REFUSE.getCode())) {
            //修改订单状态
            withdrawOrder.setStatus(WithdrawOrderStatus.REFUSE.getCode());
            if (StringUtils.isNotEmpty(checkWithdrewOrderReqDto.getReason())) {
                withdrawOrder.setReason(checkWithdrewOrderReqDto.getReason());
            }
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
    public BaseResponse withdraw(WithdrawReqDto withdrawReqDto) {
        LambdaQueryWrapper<WithdrawConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WithdrawConfig::getCoinName, withdrawReqDto.getCoinName());
        WithdrawConfig one = withdrawConfigService.getOne(lambdaQueryWrapper);

        BigDecimal amount = withdrawReqDto.getAmount();
        if (amount.compareTo(one.getMaxWithdraw()) > 0 && amount.compareTo(one.getMinWithdraw()) < 0) {
            return BaseResponse.error(MessageUtils.message("提币数量不在范围内"));
        }
        WithdrawOrder withdrawOrder = new WithdrawOrder();
        withdrawOrder.setAddress(withdrawReqDto.getToAddress());
        withdrawOrder.setAmount(amount);
        withdrawOrder.setCoinName(withdrawReqDto.getCoinName());
        //计算手续费
        BigDecimal fee = amount.multiply(one.getFeeRate()).add(one.getFee());
        withdrawOrder.setFee(fee);
        withdrawOrder.setCoinType(one.getCoinType());
        withdrawOrder.setMainCoinType(one.getMainCoinType());
        withdrawOrder.setCreateBy(ContextHandler.getUserName());
        baseMapper.insert(withdrawOrder);
        return BaseResponse.success();
    }
}
