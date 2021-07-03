package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.req.OtcSettingReqDto;
import com.ezcoins.project.otc.mapper.EzAdvertisingBusinessMapper;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import lombok.var;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
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
        String userId = ContextHandler.getUserId();
        //判断是否修改过
        LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        EzAdvertisingBusiness advertisingBusiness = baseMapper.selectOne(queryWrapper);
        advertisingBusiness.setSecurityPassword(securityPassword);
        advertisingBusiness.setUserId(ContextHandler.getUserId());
        if (StringUtils.isNotEmpty(name) && advertisingBusiness.getAdvertisingName().equals(userId)) {
            LambdaQueryWrapper<EzAdvertisingBusiness> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(EzAdvertisingBusiness::getAdvertisingName, name);// 判断昵称是否纯在
            Integer count = baseMapper.selectCount(queryWrapper1);
            if (count > 0) {
                return BaseResponse.error(MessageUtils.message("昵称重复，请重新输入"));
            }
            advertisingBusiness.setAdvertisingName(name);
            baseMapper.updateById(advertisingBusiness);
            return BaseResponse.success().message(MessageUtils.message("安全密码修改成功"));
        }
        baseMapper.updateById(advertisingBusiness);
        return BaseResponse.success();
    }


    /**
     * @param sellUserId
     * @param buyUserId
     * @Description:
     * @Param:
     * @return:
     * @Author: Wanglei
     * @Date: 2021/6/19
     */
    @Override
    public void updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime) {
        //根据用户查询到OTC详情
        LambdaQueryWrapper<EzAdvertisingBusiness> sell = new LambdaQueryWrapper<>();
        sell.eq(EzAdvertisingBusiness::getUserId, sellUserId);
        EzAdvertisingBusiness sellInfo = baseMapper.selectOne(sell);
        sellInfo.setSellCount(sellInfo.getSellCount() + 1);

        LambdaQueryWrapper<EzAdvertisingBusiness> buy = new LambdaQueryWrapper<>();
        buy.eq(EzAdvertisingBusiness::getUserId, buyUserId);
        baseMapper.selectOne(buy);
        EzAdvertisingBusiness buyInfo = baseMapper.selectOne(sell);
        buyInfo.setBuyCount(sellInfo.getBuyCount() + 1);

        Long releaseTime = finishTime.getTime() - payTime.getTime();//放行时间
        var time = Math.floor(releaseTime / 60 % 60);
        buyInfo.setMouthAveragePass((time + sellInfo.getMouthAveragePass() * sellInfo.getSellCount()) / (sellInfo.getSellCount() + 1));//平均放行时间
    }
}
