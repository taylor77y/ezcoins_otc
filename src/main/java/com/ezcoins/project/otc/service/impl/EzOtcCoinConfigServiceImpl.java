package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.EzOtcCoinConfig;
import com.ezcoins.project.otc.entity.resp.CoinConfigRespDto;
import com.ezcoins.project.otc.mapper.EzOtcCoinConfigMapper;
import com.ezcoins.project.otc.service.EzOtcCoinConfigService;
import com.ezcoins.utils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EzOtcCoinConfigServiceImpl extends ServiceImpl<EzOtcCoinConfigMapper, EzOtcCoinConfig> implements EzOtcCoinConfigService {

    /**
     * 查询所有 coin 挂单配置信息
     *
     * @param
     * @return
     */
    @Override
    public List<CoinConfigRespDto> getAllOTCTransctionConfig(String transactionType) {
        LambdaQueryWrapper<EzOtcCoinConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(EzPaymentBank::getBankName,withdrawalConfigId);
        lambdaQueryWrapper.eq(EzOtcCoinConfig::getTransactionType, transactionType);
        List<EzOtcCoinConfig> lists = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtils.copyListProperties(lists, CoinConfigRespDto::new);
    }
}
