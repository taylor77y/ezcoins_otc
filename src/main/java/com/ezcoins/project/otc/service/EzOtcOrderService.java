package com.ezcoins.project.otc.service;

import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.OrderOperateReqDto;
import com.ezcoins.project.otc.entity.req.OtcOrderReqDto;
import com.ezcoins.project.otc.entity.req.PlaceOrderReqDto;
import com.ezcoins.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface EzOtcOrderService extends IService<EzOtcOrder> {

    void releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto);

    /**
    * @Description: 下单
    * @Param: [placeOrderReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/18
    */
    BaseResponse placeAnOrder(PlaceOrderReqDto placeOrderReqDto);

    
    
    /*** 
    * @Description: 商户 下架广告订单
    * @Param: [orderNo]
    * @return: com.ezcoins.response.BaseResponse
    * @Author: Wanglei
    * @Date: 2021/6/18
    */
    BaseResponse offShelfOrder(String orderNo);




    /***
    * @Description: 商户 接单(订单广告)
    * @Param: [matchOrderNo]
    * @return: com.ezcoins.response.BaseResponse
    * @Author: Wanglei
    * @Date: 2021/6/18
    */
    BaseResponse merchantOrder(OrderOperateReqDto orderOperateReqDto);




}
