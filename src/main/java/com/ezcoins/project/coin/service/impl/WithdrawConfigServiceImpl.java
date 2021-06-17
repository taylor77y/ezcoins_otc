package com.ezcoins.project.coin.service.impl;

import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.project.coin.entity.WithdrawConfig;
import com.ezcoins.project.coin.entity.req.WithdrewConfigReqDto;
import com.ezcoins.project.coin.mapper.WithdrawConfigMapper;
import com.ezcoins.project.coin.service.WithdrawConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.otc.entity.EzPaymentMethod;
import com.ezcoins.utils.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 提现配置表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-17
 */
@Service
public class WithdrawConfigServiceImpl extends ServiceImpl<WithdrawConfigMapper, WithdrawConfig> implements WithdrawConfigService {

    /***
     * @Description: 添加/修改  提币配置
     * @Param: [withdrewConfigReqDto]
     * @return: void
     * @Author: Wanglei
     * @Date: 2021/6/17
     * @param withdrewConfigReqDto
     */
    @Override
    public void addOrUpdate(WithdrewConfigReqDto withdrewConfigReqDto) {
        Integer id = withdrewConfigReqDto.getId();
        WithdrawConfig withdrawConfig = new WithdrawConfig();
        BeanUtils.copyBeanProp(withdrawConfig,withdrewConfigReqDto);
        if (null==id){//添加
            withdrawConfig.setCreateBy(ContextHandler.getUserName());
            CheckException.checkDb(baseMapper.insert(withdrawConfig),"添加提币配置失败",() -> {
                log.error("添加提币配置失败");
            });
        }else {
            withdrawConfig.setUpdateBy(ContextHandler.getUserName());
            CheckException.checkDb(baseMapper.updateById(withdrawConfig),"提币配置更新失败",() -> {
                log.error("提币配置更新失败");
            });
        }
    }
}
