package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.RechargeConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.req.RechargeConfigReqDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface RechargeConfigService extends IService<RechargeConfig> {


    void addOrUpdate(RechargeConfigReqDto rechargeConfigReqDto);
}
