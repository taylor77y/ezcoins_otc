package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.OtcFiatCurrency;
import com.ezcoins.project.otc.mapper.OtcFiatCurrencyMapper;
import com.ezcoins.project.otc.service.OtcFiatCurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtcFiatCurrencyServiceImpl extends ServiceImpl<OtcFiatCurrencyMapper, OtcFiatCurrency> implements OtcFiatCurrencyService {

    /**
     * 法币币种列表
     *
     * @param
     * @return
     */
    @Override
    public List<OtcFiatCurrency> fiatList() {
        LambdaQueryWrapper<OtcFiatCurrency> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        return baseMapper.selectList(lambdaQueryWrapper);
    }
}
