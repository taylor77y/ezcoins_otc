package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzOtcCoinExchange;

public interface EzOtcCoinExchangeService extends IService<EzOtcCoinExchange> {

    /**
     * 查询所有 coin 挂单配置信息
     * @param
     * @return
     */
    EzOtcCoinExchange queryByCoinIdAndAbbreviation(String abbreviation, String coinId);
}
