package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.EzOtcCoinExchange;
import com.ezcoins.project.otc.mapper.EzOtcCoinExchangeMapper;
import com.ezcoins.project.otc.service.EzOtcCoinExchangeService;
import org.springframework.stereotype.Service;

@Service
public class EzOtcCoinExchangeServiceImpl extends ServiceImpl<EzOtcCoinExchangeMapper, EzOtcCoinExchange> implements EzOtcCoinExchangeService {

    /**
     * 查询所有 coin 挂单配置信息
     *
     * @param
     * @return
     */
    @Override
    public EzOtcCoinExchange queryByCoinIdAndAbbreviation(String abbreviation, String coinId) {
        LambdaQueryWrapper<EzOtcCoinExchange> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzOtcCoinExchange::getAbbreviation,abbreviation);
        lambdaQueryWrapper.eq(EzOtcCoinExchange::getExchangeCoinId, coinId);
//        List<EzOtcCoinExchange> lists = baseMapper.selectOne(lambdaQueryWrapper);
//        return BeanUtils.copyListProperties(lists, CoinConfigRespDto::new);
        return baseMapper.selectOne(lambdaQueryWrapper);
    }
}
