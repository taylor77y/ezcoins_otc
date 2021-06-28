package com.ezcoins.project.otc.service;

import com.ezcoins.project.otc.entity.EzSellConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.response.BaseResponse;

/**
 * <p>
 * 一键卖币配置 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
public interface EzSellConfigService extends IService<EzSellConfig> {


    /**
     * 一键卖币
     * @param sellOneKeyReqDto
     * @return
     */
    BaseResponse sellOneKey(SellOneKeyReqDto sellOneKeyReqDto);
}
