package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.project.config.entity.EzCountryConfig;
import com.ezcoins.project.config.service.EzCountryConfigService;
import com.ezcoins.project.otc.entity.EzOtcOrderIndex;
import com.ezcoins.project.otc.mapper.EzOtcOrderIndexMapper;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 广告订单号自增表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
@Service
public class EzOtcOrderIndexServiceImpl extends ServiceImpl<EzOtcOrderIndexMapper, EzOtcOrderIndex> implements EzOtcOrderIndexService {


    @Autowired
    private EzCountryConfigService countryConfigService;

    @Override
    public String getOrderNoByCountryCode(String countryCode,String id) {
        EzOtcOrderIndex index = baseMapper.selectById(id);
        Integer currentValue = index.getCurrentValue();
        String orderNo = OrderNoUtils.getOrderNo();
        Integer other = index.getOther();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(other);
        stringBuilder.append(orderNo);

        //订单拼串
        String sequenceStr = String.valueOf(currentValue);
        for (int i = 0; i <6 - sequenceStr.length(); i++) {
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);

        int nextSequence = index.getCurrentValue()+index.getStep() ;
        if (nextSequence >= 1000000) {
            index.setCurrentValue(0);
            baseMapper.updateById(index);
        }else {
            index.setCurrentValue(nextSequence);
            baseMapper.updateById(index);
        }
        return String.valueOf(stringBuilder);
    }

    /**
     * 根据国家货币获取订单号
     *
     * @param currencyCode
     * @param id
     */
    @Override
    public String getOrderNoByCurrencyCode(String currencyCode, String id) {
        LambdaQueryWrapper<EzCountryConfig> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(EzCountryConfig::getCurrencyCode, currencyCode);
        EzCountryConfig one = countryConfigService.getOne(configLambdaQueryWrapper);
        String countryCode = one.getCountryCode();//国家编号
        return getOrderNoByCountryCode(countryCode,id);
    }
}
