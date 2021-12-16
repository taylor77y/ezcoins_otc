package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzOtcCoinConfig;
import com.ezcoins.project.otc.entity.resp.CoinConfigRespDto;

import java.util.List;

public interface EzOtcCoinConfigService extends IService<EzOtcCoinConfig> {

    /**
     * 查询所有 coin 挂单配置信息
     * @param transactionType
     * @return
     */
    List<CoinConfigRespDto> getAllOTCTransctionConfig(String transactionType);
}
