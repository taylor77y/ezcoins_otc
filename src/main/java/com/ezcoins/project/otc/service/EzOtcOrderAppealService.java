package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcOrderAppeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.AppealReqDto;
import com.ezcoins.project.otc.entity.req.DoAppealReqDto;
import com.ezcoins.response.BaseResponse;

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
    BaseResponse appeal(AppealReqDto appealReqDto);

    /**
     * 取消申诉
     * @param orderMatchNo
     * @return
     */
    BaseResponse cancelAppeal(String orderMatchNo);

    /**
     * 处理投诉
     * @param doAppealReqDto
     * @return
     */
    BaseResponse doAppeal(DoAppealReqDto doAppealReqDto);
}
