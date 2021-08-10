package com.ezcoins.project.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.req.CheckCodeReqDto;
import com.ezcoins.project.consumer.entity.req.EzUserReqDto;
import com.ezcoins.project.consumer.entity.req.UserLimitReqDto;
import com.ezcoins.project.consumer.entity.req.VerificationCodeReqDto;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wanglei
 * @since 2021-05-26
 */
public interface EzUserService extends IService<EzUser> {

    /**
     * 检查用户唯一性
     * @param userName
     * @param phone
     * @param email
     * @param inviteCode
     * @return
     */
    boolean checkUserUnique(String userName,String phone,String phoneArea,String email,String inviteCode);


    /**
     * 获取验证码
     * @param inviteCode
     * @return
     */
    String getInviteCode(String inviteCode);

    /**
     * 根据用户名/手机号查询用户
     * @param userName
     * @param phone
     * @return
     */
    EzUser selectUserBy(String userName, String phone, String phoneArea,String email, String inviteCode);

    /**
     * 发送验证码
     * @param codeReqDto
     */
    void sendMsm(VerificationCodeReqDto codeReqDto);

    /**
     * 注册用户
     * @param ezUserDto
     */
    void addUser(EzUserReqDto ezUserDto,boolean isAdmin);

    /**
     * 用户登录
     * @param
     * @return
     */
    Map<String,String> login(JwtAuthenticationRequest jwtAuthenticationRequest);

    /**
     * 更改基本资料密码
     *
     * @param ezUserDto
     * @return
     */
    void updateUser(EzUserReqDto ezUserDto);



    void checkCode(CheckCodeReqDto checkCodeReqDto);


//    /**
//     * 封禁 解封 账号
//     * @param userLimitReqDto
//     */
//    void titleOrUnnumber(UserLimitReqDto userLimitReqDto);
//
 }
