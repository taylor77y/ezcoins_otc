package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcOrderIndex;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 广告订单号自增表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
public interface EzOtcOrderIndexService extends IService<EzOtcOrderIndex> {

    /**
     * 根据国家代码获取订单号
     * @param countryCode
     * @param id
     * @return
     */
    String getOrderNoByCountryCode(String countryCode,String id);

    /**
     * 根据国家货币获取订单号
     */
    String getOrderNoByCurrencyCode(String currencyCode,String id);

}
