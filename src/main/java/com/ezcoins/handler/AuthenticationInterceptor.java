package com.ezcoins.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.Limit;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.UserConstants;
import com.ezcoins.constant.enums.LimitType;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.jwt.TokenException;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.security.util.IJWTInfo;
import com.ezcoins.security.util.JWTHelper;
import com.ezcoins.utils.DateUtils;
import com.ezcoins.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * token注意点：
 * Authorization  token
 * AuthType  类型
 */
@Component
@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JWTHelper jwtHelper;


    @Resource
    private EzUserService userService;
    @Resource
    private EzUserLimitService limitService;

    @Resource
    private EzUserLimitLogService limitLogService;


    public static boolean flag = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if (!flag) {
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AuthToken authToken = method.getAnnotation(AuthToken.class);
        if (authToken != null) {
            String token = jwtHelper.getToken(request);
            //获取token
            if (StringUtils.isEmpty(token)) {
                throw new TokenException();
            }
            //解析token
            IJWTInfo fromToken = jwtHelper.getInfoFromToken(token);
            if (StringUtils.isNull(fromToken)) {
                throw new TokenException();
            }
            log.info("用户{}", fromToken);
            //根据token存储的值，redis判断是否失效
            boolean checkToken = jwtHelper.verifyToken(fromToken, token);
            if (!checkToken) {
                throw new TokenException();
            }
            ContextHandler.setUserId(fromToken.getUserId());
            ContextHandler.setUserName(fromToken.getUserName());
            ContextHandler.setUserType(fromToken.getUserType());
            //APP权限
            if (fromToken.getUserType().equals(LoginType.APP.getType())) {
                EzUser user = userService.getById(fromToken.getUserId());
                if (null == user) {
                    throw new TokenException();
                }
                if (authToken.status()) {
                    CheckException.check("1".equals(user.getStatus()), "用户被禁止", () -> {
                        log.error("用户被禁止");
                    });
                }
                if (authToken.advertisingStatus()) {
                    CheckException.check("1".equals(user.getLevel()), "请先进行高级认证", () -> {
                        log.error("请先进行高级认证");
                    });
                }
                if (authToken.kyc()) {
                    CheckException.check("1".equals(user.getKycStatus()), "请先完成实名认证",() -> {
                        log.error("请先完成实名认证");
                    });
                }
                Limit limitAnnotation = handlerMethod.getBeanType().getAnnotation(Limit.class);
                if (limitAnnotation != null) {
                    LambdaQueryWrapper<EzUserLimitLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(EzUserLimitLog::getIsExpire, "0");
                    lambdaQueryWrapper.eq(EzUserLimitLog::getUserId, fromToken.getUserId());
                    lambdaQueryWrapper.eq(EzUserLimitLog::getType, limitAnnotation.LIMIT_TYPE().getCode());
                    EzUserLimitLog one = limitLogService.getOne(lambdaQueryWrapper);
                    if (one != null) {
                        if (one.getBanTime() != null && one.getBanTime().getTime() < DateUtils.getNowDate().getTime()) {
                            one.setIsExpire("1");
                            limitLogService.updateById(one);
                            LambdaUpdateWrapper<EzUserLimit> queryWrapper = new LambdaUpdateWrapper<>();
                            queryWrapper.eq(EzUserLimit::getUserId, fromToken.getUserId());
                            if (limitAnnotation.LIMIT_TYPE().equals(LimitType.LOGINLIMIT)) {
                                queryWrapper.eq(EzUserLimit::getLogin, 0);
                            } else if (limitAnnotation.LIMIT_TYPE().equals(LimitType.WITHDRAWLIMIT)) {
                                queryWrapper.eq(EzUserLimit::getWithdraw, 0);
                            } else if (limitAnnotation.LIMIT_TYPE().equals(LimitType.ORDERLIMIT)) {
                                queryWrapper.eq(EzUserLimit::getOrder, 0);
                            } else if (limitAnnotation.LIMIT_TYPE().equals(LimitType.BUSINESSLIMIT)) {
                                queryWrapper.eq(EzUserLimit::getBusiness, 0);
                            }
                            limitService.update(queryWrapper);
                            return true;
                        }
                        throw new BaseException("用户行为已被限制");
                    }
                } else if (fromToken.getUserType().equals(LoginType.WEB.getType())) {
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {
        ContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
