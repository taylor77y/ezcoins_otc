package com.ezcoins.project.otc.service;

import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.project.otc.entity.EzOtcOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.*;
import com.ezcoins.project.otc.entity.resp.NewOrderRespDto;
import com.ezcoins.project.otc.entity.resp.OrderInfo;
import com.ezcoins.project.otc.entity.resp.OtcOrderRespDto;
import com.ezcoins.project.otc.entity.resp.PaymentDetails;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.response.Response;
import com.ezcoins.response.ResponseList;
import com.ezcoins.response.ResponsePageList;
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

    BaseResponse releaseAdvertisingOrder(OtcOrderReqDto otcOrderReqDto);

    /**
    * @Description: 下单
    * @Param: [placeOrderReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/18
    */
    Response<PaymentDetails> placeAnOrder(PlaceOrderReqDto placeOrderReqDto);

    
    
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


    /**
     * app订单列表
     * @param orderQueryReqDto
     * @return
     */
    ResponseList<OtcOrderRespDto> otcOrderList(OtcOrderQueryReqDto orderQueryReqDto);


    /***
    * @Description: 新订单
    * @Param: []
    * @return: com.ezcoins.response.ResponseList<com.ezcoins.project.otc.entity.resp.OtcOrderRespDto>
    * @Author: Wanglei
    * @Date: 2021/6/22
    */
    ResponseList<NewOrderRespDto> nowOrderList(PageQuery pageQuery);

    
    /*** 
    * @Description: 购买查询订单详情
    * @Param: [otcOrderNo]
    * @return: com.ezcoins.response.Response<com.ezcoins.project.otc.entity.resp.NewOrderRespDto>
    * @Author: Wanglei
    * @Date: 2021/6/22
    */
    Response<OrderInfo> orderInfo(String otcOrderNo);
}
