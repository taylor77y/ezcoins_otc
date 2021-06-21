package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.OtcSettingReqDto;
import com.ezcoins.response.BaseResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
public interface EzAdvertisingBusinessService extends IService<EzAdvertisingBusiness> {


    /**
    * @Description: 完善otc交易信息
    * @Param: [otcSettingReqDto]
    * @return: com.ezcoins.response.BaseResponse
    * @Author: Wanglei
    * @Date: 2021/6/19
    */
    BaseResponse otcSetting(OtcSettingReqDto otcSettingReqDto);

    /**
    * @Description:
    * @Param:
    * @return:
    * @Author: Wanglei
    * @Date: 2021/6/19
    */







}
