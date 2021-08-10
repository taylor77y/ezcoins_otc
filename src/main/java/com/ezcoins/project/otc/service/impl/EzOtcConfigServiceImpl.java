package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.CoinConstant;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.project.coin.entity.Type;
import com.ezcoins.project.coin.service.TypeService;
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.EzOtcConfig;
import com.ezcoins.project.otc.mapper.EzOtcConfigMapper;
import com.ezcoins.project.otc.service.EzOtcConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.Response;
import com.ezcoins.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * otc配置 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-21
 */
@Service
public class EzOtcConfigServiceImpl extends ServiceImpl<EzOtcConfigMapper, EzOtcConfig> implements EzOtcConfigService {

    @Autowired
    private TypeService typeService;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void checkOtcStatus(String userId,Integer maxCancelNum) {
        Object object = redisCache.getCacheObject(RedisConstants.CANCEL_ORDER_COUNT_KEY + userId);
        if (object != null && (Integer) object > maxCancelNum) {//5后面从配置数据库得到
            throw new BaseException("你今天取消次数超过上线,每天再来");
        }
    }
}
