package com.ezcoins.project.acl.controller;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.IgnoreUserToken;
import com.ezcoins.aspectj.lang.annotation.NoRepeatSubmit;
import com.ezcoins.base.BaseController;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.constant.enums.user.UserStatus;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.exception.user.UserException;
import com.ezcoins.exception.user.UserPasswordNotMatchException;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.entity.req.JwtAuthenticationRequest;
import com.ezcoins.project.acl.mapper.MenuMapper;
import com.ezcoins.project.acl.service.AclUserService;
import com.ezcoins.project.common.mq.producer.LoginProducer;
import com.ezcoins.redis.RedisCache;
import com.ezcoins.response.BaseResponse;
import com.ezcoins.security.util.JWTHelper;
import com.ezcoins.security.util.JWTInfo;
import com.ezcoins.utils.EncoderUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/6/2 10:06
 * @Version:1.0
 */
@Api(tags = "Admin-管理员用户登录登出")
@RestController
@RequestMapping("/admin/acl/validate")
@Slf4j
public class LoginController extends BaseController {
    @Autowired
    AclUserService aclUserService;
    @Autowired
    JWTHelper jwtHelper;

    @Autowired
    private AclUserService userService;


    @Autowired
    LoginProducer loginProducer;


    @Autowired
    private MenuMapper menuMapper;


    @Autowired
    private RedisCache redisCache;

    @PostMapping("login")
    @ApiOperation(value = "管理员用户登录")
    @IgnoreUserToken
    @NoRepeatSubmit
    public BaseResponse login(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        AclUser aclUser = aclUserService.selectByUsername(authenticationRequest.getUsername());
        //用户不存在
        if (aclUser == null || UserStatus.DELETED.getCode().equals(aclUser.getIsDeleted())) {
            throw new UserException("登录用户不存在", null);
        }
        //密码错误
        if (!EncoderUtil.matches(authenticationRequest.getPassword(), aclUser.getPassword())) {
            throw new UserPasswordNotMatchException();
        }
        //创建token
        JWTInfo jwtInfo = new JWTInfo(aclUser.getUserName(), aclUser.getId(), LoginType.WEB.getType());
        String token = jwtHelper.createToken(jwtInfo);
        log.info(">>require logging... userToken:{}<<", token);
        String userId = aclUser.getId();
        loginProducer.sendMsgLoginFollowUp(aclUser.getUserName(), userId, aclUser.getNickName(), LoginType.WEB.getType());

        Map<String, Object> result = new HashMap<>(2);
        AclUser user = userService.selectByUsername(aclUser.getUserName());
        //根据用户id获取操作权限值
        if (!"admin".equals(aclUser.getUserName())) {
            List<String> permissionValueList = menuMapper.selectCodeValueByUserId(user.getId());
            redisCache.setCacheObject(RedisConstants.LOGIN_MENU_CODE + aclUser.getUserName(), permissionValueList);
        }
        result.put("name", user.getUserName());
        result.put("token", token);
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return BaseResponse.success().data(result);
    }


    @PutMapping("out")
    @ApiOperation(value = "管理员用户退出")
    @AuthToken
    public BaseResponse out() {
        if (!"admin".equals( getUserName())) {
            redisCache.deleteObject(RedisConstants.LOGIN_MENU_CODE + getUserName());
        }
        return BaseResponse.success();
    }

}
