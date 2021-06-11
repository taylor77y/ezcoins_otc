package com.ezcoins.project.consumer.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        //查询用户key状态
        EzUser ezUser = ezUserService.getById(userKycReqDto.getUserId());
        if (ezUser.getKycStatus().equals(UserKycStatus.VERIFIED.getCode())){
            //异常  用户已认证
        }
        EzUserKyc ezcoinsKyc = new EzUserKyc();
        BeanUtils.copyBeanProp(ezcoinsKyc,userKycReqDto);
        ezcoinsKyc.setCreateBy(SecurityUtils.getUsername());
        ezcoinsKyc.setStatus(KycStatus.PENDINGREVIEW.getCode());
        baseMapper.insert(ezcoinsKyc);
    }


    /**
     * 审核
     *
     * @param kycReqDto
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void checkKyc(CheckKycReqDto kycReqDto) {
        //根据id查询审核
        EzUserKyc ezcoinsKyc = baseMapper.selectById(kycReqDto.getId());
        if (!ezcoinsKyc.getStatus().equals(KycStatus.PENDINGREVIEW.getCode())){
            //异常  用户已审核
        }
        //查询用户key状态
        EzUser ezUser = ezUserService.getById(kycReqDto.getUserId());
        if (ezUser.getKycStatus().equals(UserKycStatus.VERIFIED.getCode())){
            //异常  用户已认证
        }
        if (kycReqDto.getOperate().equals(KycStatus.BY.getCode())){
            //改变用户认证状态
            LambdaUpdateWrapper<EzUser> updateWrapper=new LambdaUpdateWrapper<>();
            updateWrapper.eq(EzUser::getUserId,kycReqDto.getUserId()).set(EzUser::getKycStatus,UserKycStatus.VERIFIED);
            ezUserService.update(null,updateWrapper);
        }
        ezcoinsKyc.setStatus(kycReqDto.getOperate());
        ezcoinsKyc.setUpdateBy(ContextHandler.getUserName());
        baseMapper.updateById(ezcoinsKyc);
    }


}
