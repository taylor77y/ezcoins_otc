package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.EzOtcCoinInfo;

import java.util.List;

public interface EzOtcCoinInfoService extends IService<EzOtcCoinInfo> {

    /**
     * 查询所有 coin 挂单配置信息
     * @param
     * @return
     */
    List<EzOtcCoinInfo> queryAllCoins();
}
