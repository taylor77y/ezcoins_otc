package com.ezcoins.project.otc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.OtcConfig;
import com.ezcoins.project.otc.entity.req.OtcConfigReqDto;
import com.ezcoins.response.Response;

public interface OtcConfigService extends IService<OtcConfig> {

    /***
     * @Description: 添加/修改 网络账号信息
     * @Param: [addrReqDto]
     * @return: com.ezcoins.response.Response
     * @Author: Wanglei
     * @Date: 2021/7/8
     */
    Response addOrUpdateOtcConfig(OtcConfigReqDto otcConfigReqDto);
}
