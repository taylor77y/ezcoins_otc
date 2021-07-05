package com.ezcoins.project.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.constant.CoinConstant;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.mapper.TypeMapper;
import com.ezcoins.project.coin.service.TypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.MessageUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 币种类型表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {


    @Override
    public boolean statusService(Type coinType,Integer type) {
        if (type.equals(CoinConstant.OTC_STATUS)) {
            if ("1".equals(coinType.getOtcStatus())) {
                return false;
            } else {
                return true;
            }
        }
        if (type.equals(CoinConstant.COIN_STATUS)) {
            if ("1".equals(coinType.getStatus())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}