package com.ezcoins.project.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.CheckKycReqDto;
import com.ezcoins.project.consumer.entity.req.UserKycReqDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-05-27
 */
public interface EzUserKycService extends IService<EzUserKyc> {

    /**
     * 用户实名认证
     * @param userKycReqDto 实体
     */
    void verified(UserKycReqDto userKycReqDto);

    /**
     * 审核
     * @param kycReqDto
     */
    void checkKyc(CheckKycReqDto kycReqDto);


    /**
     * 获取审核通过的数据
     * @param userId
     * @return
     */
    EzUserKyc getOneApprove(String userId);
}
