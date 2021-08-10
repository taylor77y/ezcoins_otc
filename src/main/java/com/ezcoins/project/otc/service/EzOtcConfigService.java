package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzOtcConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * otc配置 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-21
 */
public interface EzOtcConfigService extends IService<EzOtcConfig> {


    /**
     *
     * @param userId
     */
    //     * @param type 一键卖币开关  0：普通买卖 上架  1：一键卖币
    void checkOtcStatus( String userId,Integer maxCancelNum);
}
