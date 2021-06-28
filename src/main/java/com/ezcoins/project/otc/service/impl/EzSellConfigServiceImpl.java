package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.Constants;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.otc.entity.EzSellConfig;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.project.otc.mapper.EzSellConfigMapper;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 一键卖币配置 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Service
public class EzSellConfigServiceImpl extends ServiceImpl<EzSellConfigMapper, EzSellConfig> implements EzSellConfigService {


    @Autowired
    private AccountService accountService;


    /**
     * 一键卖币
     *
     * @param sellOneKeyReqDto
     * @return
     */
    @Override
    public BaseResponse sellOneKey(SellOneKeyReqDto sellOneKeyReqDto) {
        LambdaQueryWrapper<EzSellConfig> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzSellConfig::getCoinName,sellOneKeyReqDto.getCoinName());
        EzSellConfig ezSellConfig = baseMapper.selectOne(queryWrapper);
        if ("1".equals(ezSellConfig.getStatus())){
            return BaseResponse.error("当前币种一键卖币已关闭");
        }

        BigDecimal amount = sellOneKeyReqDto.getAmount();
        BigDecimal maxAmount = ezSellConfig.getMaxAmount();
        BigDecimal minAmount = ezSellConfig.getMinAmount();

        if (amount.compareTo(maxAmount)>0 || amount.compareTo(minAmount)<0){
            throw new BaseException("输入数量错误");
        }
        //手续费计算
        BigDecimal fee=amount.multiply(ezSellConfig.getFeeRatio()).setScale(8, RoundingMode.FLOOR).add(ezSellConfig.getFee());

        //冻结用户卖出 数量
        List<BalanceChange> cList = new ArrayList<>();
        BalanceChange b = new BalanceChange();
        String userId = ContextHandler.getUserId();
        BigDecimal total = amount.add(fee);

        b.setAvailable(total);
        b.setCoinName(sellOneKeyReqDto.getCoinName());
        b.setUserId(userId);
        b.setIncomeType(CoinConstants.IncomeType.PAYOUT.getType());
        b.setFrozen(total);
        b.setFee(fee);
        b.setMainType(CoinConstants.MainType.FROZEN.getType());
        accountService.balanceChangeSYNC(cList);

        //生成 订单（订单类型一键）





        return null;
    }
}
