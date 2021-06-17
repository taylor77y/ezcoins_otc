package com.ezcoins.project.coin.service;

import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.coin.entity.req.WithdrewConfigReqDto;

/**
 * <p>
 * 提现配置表 服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
public interface WithdrawConfigService extends IService<WithdrawConfig> {

    /***
    * @Description:  添加/修改  提币配置
    * @Param: [withdrewConfigReqDto]
    * @return: void
    * @Author: Wanglei
    * @Date: 2021/6/17
    */
    void addOrUpdate(WithdrewConfigReqDto withdrewConfigReqDto);
}
