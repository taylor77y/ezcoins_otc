package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.constant.enums.user.AdvertisingStatus;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.constant.enums.user.UserKycStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.coin.AccountOperationBusyException;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.ezcoins.project.consumer.entity.EzAdvertisingConfig;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.AdvertisingReqDto;
import com.ezcoins.project.consumer.entity.req.CheckAdvertisingReqDto;
import com.ezcoins.project.consumer.mapper.EzAdvertisingApproveMapper;
import com.ezcoins.project.consumer.service.EzAdvertisingApproveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.consumer.service.EzAdvertisingConfigService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.ezcoins.security.util.SecurityUtils;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 高级认证表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
@Service
public class EzAdvertisingApproveServiceImpl extends ServiceImpl<EzAdvertisingApproveMapper, EzAdvertisingApprove> implements EzAdvertisingApproveService {
    @Autowired
    private EzUserService userService;

    @Autowired
    private EzAdvertisingBusinessService businessService;

    @Autowired
    private EzAdvertisingConfigService advertisingConfigService;

    @Autowired
    private AccountService accountService;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void checkAdvertising(CheckAdvertisingReqDto checkAdvertisingReqDto) {
        //根据id查询审核
        EzAdvertisingApprove advertisingApprove = baseMapper.selectById(checkAdvertisingReqDto.getId());
        if (!advertisingApprove.getStatus().equals(AdvertisingStatus.PENDINGREVIEW.getCode())){
            throw new BaseException("已审核");
        }
        if (checkAdvertisingReqDto.getOperate().equals(AdvertisingStatus.BY.getCode())){
            //改变用户认证状态
            LambdaUpdateWrapper<EzUser> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(EzUser::getUserId,advertisingApprove.getUserId()).set(EzUser::getLevel, UserKycStatus.VERIFIED.getCode());
            userService.update(null,updateWrapper);

            //修改OTC信息
            LambdaUpdateWrapper<EzAdvertisingBusiness> businessUpdateWrapper=new LambdaUpdateWrapper<>();
            businessUpdateWrapper.eq(EzAdvertisingBusiness::getUserId,advertisingApprove.getUserId())
                    .set(EzAdvertisingBusiness::getPlusV,"0")
                    .set(EzAdvertisingBusiness::getMargin,advertisingApprove.getMargin());
            businessService.update(null,businessUpdateWrapper);


            //冻结用户卖出 数量
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setCoinName("USDT");
            b.setUserId(advertisingApprove.getUserId());
            b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
            b.setFrozen(advertisingApprove.getMargin().negate());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            cList.add(b);
            accountService.balanceChangeSYNC(cList);
            //给用户一个信号
            WebSocketHandle.businessAuthentication(advertisingApprove.getUserId(),KycStatus.BY.getCode());
        }else {
            advertisingApprove.setStatus(KycStatus.REFUSE.getCode());
            //冻结用户卖出 数量
            List<BalanceChange> cList = new ArrayList<>();
            BalanceChange b = new BalanceChange();
            b.setAvailable(advertisingApprove.getMargin());
            b.setCoinName("USDT");
            b.setUserId(advertisingApprove.getUserId());
            b.setIncomeType(CoinConstants.IncomeType.INCOME.getType());
            b.setFrozen(advertisingApprove.getMargin().negate());
            b.setMainType(CoinConstants.MainType.UNFREEZE.getType());
            cList.add(b);
            accountService.balanceChangeSYNC(cList);
            //给用户一个信号
            WebSocketHandle.businessAuthentication(advertisingApprove.getUserId(),KycStatus.REFUSE.getCode());
        }
        advertisingApprove.setStatus(checkAdvertisingReqDto.getOperate());
        advertisingApprove.setExamineBy(ContextHandler.getUserName());
        advertisingApprove.setExamineTime(DateUtils.getNowDate());
        baseMapper.updateById(advertisingApprove);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void verified(AdvertisingReqDto advertisingReqDto) {
        EzAdvertisingConfig config = advertisingConfigService.getById(1);
        if (config.getMinMargin().compareTo(advertisingReqDto.getMargin())>0){
            throw new BaseException(MessageUtils.message("最低数量为",config.getMinMargin()));
        }
        //查看用户是否通过实名认证
        EzUser user = userService.getById(advertisingReqDto.getUserId());
        if (user.getKycStatus().equals(UserKycStatus.NOTCERTIFIED.getCode())){
            throw new BaseException(MessageUtils.message("用户未通过实名认证"));
        }
        EzAdvertisingApprove advertisingApprove =null;
        //查看用户的实名认证
        LambdaQueryWrapper<EzAdvertisingApprove> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzAdvertisingApprove::getUserId,advertisingReqDto.getUserId());
        advertisingApprove= baseMapper.selectOne(lambdaQueryWrapper);

        //冻结用户卖出 数量
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        String userId = ContextHandler.getUserId();
        b.setAvailable(advertisingReqDto.getMargin().negate());
        b.setCoinName("USDT");
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setFrozen(advertisingReqDto.getMargin());
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        cList.add(b);
        if (!accountService.balanceChangeSYNC(cList)) {// 资产变更异常
            throw new AccountOperationBusyException();
        }
        if (null==advertisingApprove){
            advertisingApprove=new EzAdvertisingApprove();
            advertisingApprove.setMargin(advertisingReqDto.getMargin());
            advertisingApprove.setCreateBy(SecurityUtils.getUsername());
            advertisingApprove.setCreateTime(DateUtils.getNowDate());
            advertisingApprove.setStatus(KycStatus.PENDINGREVIEW.getCode());
            advertisingApprove.setUserId(ContextHandler.getUserId());
            baseMapper.insert(advertisingApprove);
        }else {
            if (advertisingApprove.getStatus().equals(KycStatus.BY.getCode())){
                throw new BaseException("用户认证已通过");
            }
            advertisingApprove.setMargin(advertisingReqDto.getMargin());
            advertisingApprove.setMemo(null);
            advertisingApprove.setExamineBy(null);
            advertisingApprove.setExamineTime(null);
            advertisingApprove.setCreateTime(DateUtils.getNowDate());
            advertisingApprove.setStatus(KycStatus.PENDINGREVIEW.getCode());
            baseMapper.updateById(advertisingApprove);
        }
        //给用户一个信号
        WebSocketHandle.businessAuthentication(advertisingReqDto.getUserId(),KycStatus.PENDINGREVIEW.getCode());
    }
}
