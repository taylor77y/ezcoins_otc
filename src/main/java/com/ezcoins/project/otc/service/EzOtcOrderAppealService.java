package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcOrderAppeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.AppealReqDto;
import com.ezcoins.project.otc.entity.req.DoAppealReqDto;
import com.ezcoins.project.otc.entity.req.DoOrderReqDto;
import com.ezcoins.response.Response;

/**
 * <p>
 * 订单申诉 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-08
 */
public interface EzOtcOrderAppealService extends IService<EzOtcOrderAppeal> {
    /**
     * 订单申诉
     * @param appealReqDto
     * @return
     */
    Response appeal(AppealReqDto appealReqDto);

    /**
     * 取消申诉
     * @param id
     * @return
     */
    Response cancelAppeal(String id);

    /**
     * 处理投诉
     * @param doAppealReqDto
     * @return
     */
    Response doAppeal(DoAppealReqDto doAppealReqDto);


    /**
     * 处理投诉订单
     * @param orderReqDto
     * @return
     */
    Response doOrder(DoOrderReqDto orderReqDto);
}
