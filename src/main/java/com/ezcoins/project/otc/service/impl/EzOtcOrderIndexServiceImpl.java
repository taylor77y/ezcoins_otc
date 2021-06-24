package com.ezcoins.project.otc.service.impl;

import com.ezcoins.project.otc.entity.EzOtcOrderIndex;
import com.ezcoins.project.otc.mapper.EzOtcOrderIndexMapper;
import com.ezcoins.project.otc.service.EzOtcOrderIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.OrderNoUtils;
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

    @Override
    public String getOrderNo(String countryCode,String id) {
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
}
