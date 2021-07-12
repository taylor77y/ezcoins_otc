package com.ezcoins.project.otc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.otc.entity.EzAdvertisingBusiness;
import com.ezcoins.project.otc.entity.req.OtcSettingReqDto;
import com.ezcoins.project.otc.mapper.EzAdvertisingBusinessMapper;
import com.ezcoins.project.otc.service.EzAdvertisingBusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.utils.EncoderUtil;
import com.ezcoins.utils.MessageUtils;
import com.ezcoins.utils.StringUtils;
import lombok.var;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        advertisingBusiness.setSecurityPassword(EncoderUtil.encode(securityPassword));
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
    public void updateCount(String sellUserId, String buyUserId, Date payTime, Date finishTime, boolean isAdmin, String status) {
        List<EzAdvertisingBusiness> businesses = new ArrayList<>();
        //根据用户查询到OTC详情
        LambdaQueryWrapper<EzAdvertisingBusiness> sell = new LambdaQueryWrapper<>();
        sell.eq(EzAdvertisingBusiness::getUserId, sellUserId);
        EzAdvertisingBusiness sellInfo = baseMapper.selectOne(sell);
        sellInfo.setSellCount(sellInfo.getSellCount() + 1);
        LambdaQueryWrapper<EzAdvertisingBusiness> buy = new LambdaQueryWrapper<>();
        buy.eq(EzAdvertisingBusiness::getUserId, buyUserId);
        EzAdvertisingBusiness buyInfo = baseMapper.selectOne(buy);
        buyInfo.setBuyCount(buyInfo.getBuyCount() + 1);

        Integer totalSell = sellInfo.getSellCount() + sellInfo.getBuyCount() - 1;//卖家完成数量
        Double finishRateSell = sellInfo.getFinishRate();//完成率
        Double totalFillSell = (1 - finishRateSell) * totalSell;//未完成数量
        Double finishSell = totalSell - totalFillSell;//卖家成功完成数量

        Integer buyCount1 = sellInfo.getBuyCount();
        Double finishBuyRate1 = sellInfo.getFinishBuyRate();
        Double totalBuyFill1 = (1 - finishBuyRate1) * buyCount1;//未完成数量
        Double finishBuyRete1 = buyCount1 - totalBuyFill1;//完成数量+1


        Integer totalBuy = buyInfo.getSellCount() + buyInfo.getBuyCount() - 1;//买家完成数量
        Double finishRateBuy = buyInfo.getFinishRate();
        Double totalFillBuy = (1 - finishRateBuy) * totalBuy;//未完成数量
        Double finishBuy = totalBuy - totalFillBuy;//买家成功完成数量


        Integer buyCount = buyInfo.getBuyCount();
        Double finishBuyRate = buyInfo.getFinishBuyRate();
        Double totalBuyFill = (1 - finishBuyRate) * buyCount;//未完成数量
        Double finishBuyRete = buyCount - totalBuyFill;//完成数量+1

        if (isAdmin) {//修改完成率
            if ("0".equals(status)) {
                sellInfo.setFinishRate(finishSell / totalSell + 1);//卖家降低完成率
                buyInfo.setFinishRate(finishBuy + 1 / totalBuy + 1);//买家提升完成率
                buyInfo.setFinishBuyRate(finishBuyRete + 1 / buyCount + 1);
            } else {
                sellInfo.setFinishRate(finishSell + 1/ totalSell + 1);//增加降低完成率
                buyInfo.setFinishRate(finishBuy/ totalBuy + 1);//买家提升完成率
                buyInfo.setFinishBuyRate(finishBuyRete/ buyCount + 1);
            }
        } else {
            Long releaseTime = finishTime.getTime() - payTime.getTime();//放行时间
            var time = Math.floor(releaseTime / 60 % 60);
            buyInfo.setAveragePass((time + sellInfo.getAveragePass() * sellInfo.getSellCount()) / (sellInfo.getSellCount() + 1));//平均放行时间

            sellInfo.setFinishRate(finishSell + 1/ totalSell + 1);//增加降低完成率
            buyInfo.setFinishRate(finishBuy+ 1/ totalBuy + 1);//买家提升完成率
            buyInfo.setFinishBuyRate(finishBuyRete+ 1/ buyCount + 1);
        }
        businesses.add(sellInfo);
        businesses.add(buyInfo);
        this.updateBatchById(businesses);
    }

    /**
     * 查看是否修改过OTC 交易信息
     *
     * @param userId
     */
    @Override
    public boolean isUpdate(String userId) {
        LambdaQueryWrapper<EzAdvertisingBusiness> businessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getAdvertisingName, userId);
        businessLambdaQueryWrapper.eq(EzAdvertisingBusiness::getUserId, userId);
        if (baseMapper.selectCount(businessLambdaQueryWrapper) == 1) {
            return false;
        }
        return true;
    }

    /**
     * 查看是否修改过OTC 交易信息
     *
     * @param id
     */
    @Override
    public boolean isUpdateBy(String id) {
        EzAdvertisingBusiness advertisingBusiness = baseMapper.selectById(id);
        if (advertisingBusiness.getUserId().equals(advertisingBusiness.getAdvertisingName())) {
            return false;
        }
        return true;
    }
}
