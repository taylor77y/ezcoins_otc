package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.constant.enums.LimitType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.mapper.EzUserLimitMapper;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.response.Response;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 封号列表 服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-07-26
 */
@Service
public class EzUserLimitServiceImpl extends ServiceImpl<EzUserLimitMapper, EzUserLimit> implements EzUserLimitService {

    @Autowired
   private EzUserService userService;
    @Autowired
   private EzUserLimitLogService limitLogService;

    //封禁类别（0：登录封禁 1：提现封禁 2：发布广告封禁 3：买卖封禁）
    @Override
    @Transactional
    public Response title(UserLimitReqDto userLimitReqDto) {
        EzUserLimit ezUserLimit = baseMapper.selectById(userLimitReqDto.getUserId());
        EzUser user = userService.getById(userLimitReqDto.getUserId());
        boolean f=false;
        if (null==ezUserLimit){
            f=true;
            ezUserLimit=new EzUserLimit();
        }
        String type = userLimitReqDto.getType();
        if (LimitType.LOGINLIMIT.getCode().equals(type)){
            if (!f && "1".equals(ezUserLimit.getLogin()) ){
                return Response.error("用户登录已被封禁");
            }
            ezUserLimit.setLogin("1");
            user.setStatus("1");
            userService.updateById(user);
        }else if (LimitType.WITHDRAWLIMIT.getCode().equals(type)){
            if (!f && "1".equals(ezUserLimit.getWithdraw()) ){
                return Response.error("用户提现已被封禁");
            }
            ezUserLimit.setWithdraw("1");
        }else if (LimitType.ORDERLIMIT.getCode().equals(type)){
            if (!f && "1".equals(ezUserLimit.getOrder()) ){
                return Response.error("用户发布订单已被封禁");
            }
            ezUserLimit.setOrder("1");
        }else if (LimitType.BUSINESSLIMIT.getCode().equals(type)){
            if (!f && "1".equals(ezUserLimit.getBusiness()) ){
                return Response.error("用户买卖已被封禁");
            }
            ezUserLimit.setBusiness("1");
        }
        EzUserLimitLog ezUserLimitLog = new EzUserLimitLog();
        ezUserLimitLog.setUserId(user.getUserId());
        ezUserLimitLog.setUserName(user.getUserName());
        ezUserLimitLog.setType(type);
        ezUserLimitLog.setCreateBy(ContextHandler.getUserName());
        ezUserLimitLog.setDetailed(userLimitReqDto.getDetailed());
        if (StringUtils.isNotNull(userLimitReqDto.getDay())){
            ezUserLimitLog.setBanTime(DateUtils.getNdayStart(userLimitReqDto.getDay()));
        }
        ezUserLimit.setUserId(user.getUserId());
        ezUserLimit.setUserName(user.getUserName());
        if (f){
            baseMapper.insert(ezUserLimit);
        }else {
            baseMapper.updateById(ezUserLimit);
        }
        limitLogService.save(ezUserLimitLog);
        return Response.success();
    }


    @Override
    @Transactional
    public Response unblock(String userId, String type) {
        EzUserLimit ezUserLimit = baseMapper.selectById(userId);
        if (LimitType.LOGINLIMIT.getCode().equals(type)){
            if ("0".equals(ezUserLimit.getLogin()) ){
                return Response.error("用户登录未被封禁");
            }
            LambdaUpdateWrapper<EzUser> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(EzUser::getUserId,userId);
            updateWrapper.set(EzUser::getStatus,"0");
            userService.update(updateWrapper);
            ezUserLimit.setLogin("0");
        }else if (LimitType.WITHDRAWLIMIT.getCode().equals(type)){
            if ("0".equals(ezUserLimit.getWithdraw()) ){
                return Response.error("用户提现未被封禁");
            }
            ezUserLimit.setWithdraw("0");
        }else if (LimitType.ORDERLIMIT.getCode().equals(type)){
            if ("0".equals(ezUserLimit.getOrder()) ){
                return Response.error("用户发布订单未被封禁");
            }
            ezUserLimit.setOrder("0");
        }else if (LimitType.BUSINESSLIMIT.getCode().equals(type)){
            if ( "0".equals(ezUserLimit.getBusiness()) ){
                return Response.error("用户买卖未被封禁");
            }
            ezUserLimit.setBusiness("0");
        }
        baseMapper.updateById(ezUserLimit);
        LambdaUpdateWrapper<EzUserLimitLog> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(EzUserLimitLog::getUserId,userId);
        lambdaUpdateWrapper.eq(EzUserLimitLog::getIsExpire,"0");
        lambdaUpdateWrapper.set(EzUserLimitLog::getIsExpire,"1");
        limitLogService.update(lambdaUpdateWrapper);
        return Response.success();
    }
}
