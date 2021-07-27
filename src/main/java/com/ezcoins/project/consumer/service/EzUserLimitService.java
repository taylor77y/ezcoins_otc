package com.ezcoins.project.consumer.service;

import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.response.Response;

/**
 * <p>
 * 封号列表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-26
 */
public interface EzUserLimitService extends IService<EzUserLimit> {


    Response title(UserLimitReqDto userLimitReqDto);


    Response unblock(String userId, String type);
}
