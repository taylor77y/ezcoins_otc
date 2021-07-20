package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.user.KycStatus;
import com.ezcoins.constant.enums.user.UserKycStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserKyc;
import com.ezcoins.project.consumer.entity.req.CheckKycReqDto;
import com.ezcoins.project.consumer.entity.req.UserKycReqDto;
import com.ezcoins.project.consumer.mapper.EzUserKycMapper;
import com.ezcoins.project.consumer.service.EzUserKycService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.security.util.SecurityUtils;
import com.ezcoins.utils.BeanUtils;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.websocket.WebSocketHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wanglei
 * @since 2021-05-27
 */
@Service
public class EzUserKycServiceImpl extends ServiceImpl<EzUserKycMapper, EzUserKyc> implements EzUserKycService {



    @Autowired
    private EzUserService ezUserService;

    /**
     * 用户实名认证
     *
     */
    @Override
    public void verified(UserKycReqDto userKycReqDto) {
         //查看用户的实名认证
        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUserKyc::getUserId,userKycReqDto.getUserId());
        EzUserKyc ezcoinsKyc = baseMapper.selectOne(lambdaQueryWrapper);
        if (null==ezcoinsKyc){
            ezcoinsKyc=new EzUserKyc();
            BeanUtils.copyBeanProp(ezcoinsKyc,userKycReqDto);
            ezcoinsKyc.setCreateBy(SecurityUtils.getUsername());
            ezcoinsKyc.setCreateTime(DateUtils.getNowDate());
            ezcoinsKyc.setStatus(KycStatus.PENDINGREVIEW.getCode());
            baseMapper.insert(ezcoinsKyc);
        }else {
            if (ezcoinsKyc.getStatus().equals(KycStatus.BY.getCode())){
                throw new BaseException("用户认证已通过");
            }
            BeanUtils.copyBeanProp(ezcoinsKyc,userKycReqDto);
            ezcoinsKyc.setMemo(null);
            ezcoinsKyc.setExamineBy(null);
            ezcoinsKyc.setExamineTime(null);
            ezcoinsKyc.setCreateTime(DateUtils.getNowDate());
            ezcoinsKyc.setStatus(KycStatus.PENDINGREVIEW.getCode());
            baseMapper.updateById(ezcoinsKyc);
        }
        //给用户一个信号
        WebSocketHandle.realNameAuthentication(userKycReqDto.getUserId(),KycStatus.PENDINGREVIEW.getCode());
    }

    /**
     * 审核
     * @param kycReqDto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void checkKyc(CheckKycReqDto kycReqDto) {
        //根据id查询审核
        EzUserKyc ezUserKyc = baseMapper.selectById(kycReqDto.getId());
        if (!ezUserKyc.getStatus().equals(KycStatus.PENDINGREVIEW.getCode())){
            throw new BaseException("已审核");
        }
        if (kycReqDto.getOperate().equals(KycStatus.BY.getCode())){
            //改变用户认证状态
            LambdaUpdateWrapper<EzUser> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(EzUser::getUserId,ezUserKyc.getUserId()).set(EzUser::getKycStatus,UserKycStatus.VERIFIED.getCode());
            ezUserService.update(null,updateWrapper);
            //给用户一个信号
            WebSocketHandle.realNameAuthentication(ezUserKyc.getUserId(),KycStatus.BY.getCode());
        }else {
            ezUserKyc.setStatus(KycStatus.REFUSE.getCode());
            //给用户一个信号
            WebSocketHandle.realNameAuthentication(ezUserKyc.getUserId(),KycStatus.REFUSE.getCode());
        }
        ezUserKyc.setStatus(kycReqDto.getOperate());
        ezUserKyc.setExamineBy(ContextHandler.getUserName());
        ezUserKyc.setExamineTime(DateUtils.getNowDate());
        baseMapper.updateById(ezUserKyc);
    }
    /**
     * 获取审核通过的数据
     * @param userId
     * @return
     */
    @Override
    public EzUserKyc getOneApprove(String userId) {
        //根据用户id查询 kyc信息
        LambdaQueryWrapper<EzUserKyc> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EzUserKyc::getUserId, ContextHandler.getUserId());
        return baseMapper.selectOne(lambdaQueryWrapper);
    }

}
