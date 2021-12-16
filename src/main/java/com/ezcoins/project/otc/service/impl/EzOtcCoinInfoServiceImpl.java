package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.EzOtcCoinInfo;
import com.ezcoins.project.otc.mapper.EzOtcCoinInfoMapper;
import com.ezcoins.project.otc.service.EzOtcCoinInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EzOtcCoinInfoServiceImpl extends ServiceImpl<EzOtcCoinInfoMapper, EzOtcCoinInfo> implements EzOtcCoinInfoService {

    /**
     * 查询所有 coin 挂单配置信息
     *
     * @param
     * @return
     */
    @Override
    public List<EzOtcCoinInfo> queryAllCoins() {
        LambdaQueryWrapper<EzOtcCoinInfo> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzPaymentBank::getBankName,withdrawalConfigId);
//        lambdaQueryWrapper.eq(EzOtcCoinConfig::getTransactionType, transactionType);
        List<EzOtcCoinInfo> lists = baseMapper.selectList(lambdaQueryWrapper);
        return lists;
    }
}
