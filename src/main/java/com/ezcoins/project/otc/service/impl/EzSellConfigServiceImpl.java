package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.Constants;
import com.ezcoins.constant.enums.coin.CoinConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.coin.entity.vo.BalanceChange;
import com.ezcoins.project.coin.service.AccountService;
import com.ezcoins.project.otc.entity.EzOneSellConfig;
import com.ezcoins.project.otc.entity.req.SellConfigReqDto;
import com.ezcoins.project.otc.entity.req.SellOneKeyReqDto;
import com.ezcoins.project.otc.mapper.EzSellConfigMapper;
import com.ezcoins.project.otc.service.EzSellConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.Response;
import com.ezcoins.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 一键卖币配置 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-28
 */
@Service
public class EzSellConfigServiceImpl extends ServiceImpl<EzSellConfigMapper, EzOneSellConfig> implements EzSellConfigService {


    @Override
    public Response updateOrAddSellConfig(SellConfigReqDto sellConfigReqDto) {
        EzOneSellConfig ezSellConfig = new EzOneSellConfig();
        BeanUtils.copyBeanProp(ezSellConfig,sellConfigReqDto);
        if (sellConfigReqDto.getId()==null){//增加
            //判断币种是否有开启相同的币种
            LambdaQueryWrapper<EzOneSellConfig> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(EzOneSellConfig::getCoinName,sellConfigReqDto.getCoinName());
            lambdaQueryWrapper.eq(EzOneSellConfig::getStatus,"0");
            EzOneSellConfig ezOneSellConfig = baseMapper.selectOne(lambdaQueryWrapper);
            if (null!=ezOneSellConfig){
                return Response.error("该币种已存在，请先关闭原来的配置");
            }
            ezSellConfig.setCreateBy(ContextHandler.getUserName());
            baseMapper.insert(ezSellConfig);
        }else {
            ezSellConfig.setUpdateBy(ContextHandler.getUserName());
            baseMapper.updateById(ezSellConfig);
        }
        return Response.success();
    }
}
