package com.ezcoins.project.consumer.service;

import com.ezcoins.project.consumer.entity.EzAdvertisingApprove;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.consumer.entity.req.AdvertisingReqDto;
import com.ezcoins.project.consumer.entity.req.CheckAdvertisingReqDto;

/**
 * <p>
 * 高级认证表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-30
 */
public interface EzAdvertisingApproveService extends IService<EzAdvertisingApprove> {

    void checkAdvertising(CheckAdvertisingReqDto checkAdvertisingReqDto);

    /**
     * 高级认证
     * @param advertisingReqDto
     */
    void verified(AdvertisingReqDto advertisingReqDto);
}
