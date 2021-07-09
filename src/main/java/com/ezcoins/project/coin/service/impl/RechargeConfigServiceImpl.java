package com.ezcoins.project.coin.service.impl;

import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.coin.entity.RechargeConfig;
import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.ezcoins.project.coin.entity.req.RechargeConfigReqDto;
import com.ezcoins.project.coin.mapper.RechargeConfigMapper;
import com.ezcoins.project.coin.service.RechargeConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.utils.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
public class RechargeConfigServiceImpl extends ServiceImpl<RechargeConfigMapper, RechargeConfig> implements RechargeConfigService {

    @Override
    public void addOrUpdate(RechargeConfigReqDto rechargeConfigReqDto) {
        Integer id = rechargeConfigReqDto.getId();
        RechargeConfig rechargeConfig = new RechargeConfig();
        BeanUtils.copyBeanProp(rechargeConfig,rechargeConfigReqDto);
        if (null==id){//添加
            rechargeConfig.setCreateBy(ContextHandler.getUserName());
            CheckException.checkDb(baseMapper.insert(rechargeConfig),"添加充币配置失败",() -> {
                log.error("添加充币配置失败");
            });
        }else {
            rechargeConfig.setUpdateBy(ContextHandler.getUserName());
            CheckException.checkDb(baseMapper.updateById(rechargeConfig),"充币配置更新失败",() -> {
                log.error("充币配置更新失败");
            });
        }
    }
}
