package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.req.OtcSettingReqDto;
import com.ezcoins.project.otc.mapper.EzAdvertisingBusinessMapper;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-06-16
 */
@Service
public class EzAdvertisingBusinessServiceImpl extends ServiceImpl<EzAdvertisingBusinessMapper, EzAdvertisingBusiness> implements EzAdvertisingBusinessService {

    /***
     * @Description: 完善otc交易信息
     * @Param: [otcSettingReqDto]
     * @return: com.ezcoins.response.BaseResponse
     * @Author: Wanglei
     * @Date: 2021/6/19
     * @param otcSettingReqDto
     */
    @Override
    public BaseResponse otcSetting(OtcSettingReqDto otcSettingReqDto) {
        String name = otcSettingReqDto.getAdvertisingName();
        String securityPassword = otcSettingReqDto.getSecurityPassword();
        //判断昵称是否纯在
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getAdvertisingName,name);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count>0){
            BaseResponse.error("昵称重复，请重新输入");
        }
        EzAdvertisingBusiness business = new EzAdvertisingBusiness();
        business.setAdvertisingName(name);
        business.setSecurityPassword(securityPassword);
        business.setUserId(ContextHandler.getUserId());
        return BaseResponse.success();
    }
}
