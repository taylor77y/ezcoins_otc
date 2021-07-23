package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.SellConfigReqDto;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.response.Response;

/**
 * <p>
 * 一键卖币配置 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
public interface EzSellConfigService extends IService<EzOneSellConfig> {


    Response updateOrAddSellConfig(SellConfigReqDto sellConfigReqDto);
}
