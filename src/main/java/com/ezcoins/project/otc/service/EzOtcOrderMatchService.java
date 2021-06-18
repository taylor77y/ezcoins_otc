package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcOrderMatch;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.response.BaseResponse;

/**
 * <p>
 * 匹配日OTC订单 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-18
 */
public interface EzOtcOrderMatchService extends IService<EzOtcOrderMatch> {

    /***
    * @Description: 用户 取消订单（两个状态可取消订单  1：接单广告（卖家未接受订单）用户免费取消 2：接单广告/普通广告（用户未支付状态） 用户取消次数增加）
    * @Param: [matchOrderNo]
    * @return: com.ezcoins.response.BaseResponse
    * @Author: Wanglei
    * @Date: 2021/6/18
    */
    BaseResponse cancelOrder(String matchOrderNo);
}
