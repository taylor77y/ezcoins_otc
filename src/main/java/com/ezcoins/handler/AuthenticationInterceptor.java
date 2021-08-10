package com.ezcoins.handler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.enums.LimitType;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.constant.interf.RedisConstants;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.jwt.TokenException;
import com.ezcoins.exception.user.UserException;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.entity.EzUserLimit;
import com.ezcoins.project.consumer.entity.EzUserLimitLog;
import com.ezcoins.project.consumer.service.EzUserLimitLogService;
import com.ezcoins.project.consumer.service.EzUserLimitService;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.redis.RedisCache;
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
import java.util.List;

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

    @Resource
    private RedisCache redisCache;


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
                if (authToken.status() && "1".equals(user.getStatus())) {
                    redisCache.deleteObject(RedisConstants.LOGIN_USER_KEY + user.getUserId() + "_" + LoginType.APP.getType());
                    throw new BaseException("用户被禁止");
                }
                if (authToken.advertisingStatus()) {
                    CheckException.check("1".equals(user.getLevel()), "请先进行高级认证", () -> {
                        log.error("请先进行高级认证");
                    });
                }
                if (authToken.kyc() && "1".equals(user.getKycStatus())) {
                    throw new BaseException(null,"700","请先完成实名认证",null);
                }
                if (!authToken.LIMIT_TYPE().equals(LimitType.NOLIMIT)) {
                    LambdaQueryWrapper<EzUserLimitLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(EzUserLimitLog::getIsExpire, "0");
                    lambdaQueryWrapper.eq(EzUserLimitLog::getUserId, fromToken.getUserId());
                    lambdaQueryWrapper.eq(EzUserLimitLog::getType, authToken.LIMIT_TYPE().getCode());
                    EzUserLimitLog one = limitLogService.getOne(lambdaQueryWrapper);
                    if (one != null) {
                        if (one.getBanTime() != null && one.getBanTime().getTime() < DateUtils.getNowDate().getTime()) {
                            one.setIsExpire("1");
                            limitLogService.updateById(one);
                            LambdaUpdateWrapper<EzUserLimit> queryWrapper = new LambdaUpdateWrapper<>();
                            queryWrapper.eq(EzUserLimit::getUserId, fromToken.getUserId());
                            if (authToken.LIMIT_TYPE().equals(LimitType.LOGINLIMIT)) {
                                queryWrapper.set(EzUserLimit::getLogin, 0);
                                user.setStatus("0");
                                userService.updateById(user);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.WITHDRAWLIMIT)) {
                                queryWrapper.set(EzUserLimit::getWithdraw, 0);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.ORDERLIMIT)) {
                                queryWrapper.set(EzUserLimit::getOrders, 0);
                            } else if (authToken.LIMIT_TYPE().equals(LimitType.BUSINESSLIMIT)) {
                                queryWrapper.set(EzUserLimit::getBusiness, 0);
                            }
                            limitService.update(queryWrapper);
                            return true;
                        }
                        throw new BaseException("用户行为已被限制");
                    }
                }
            } else if (fromToken.getUserType().equals(LoginType.WEB.getType())) {
                if (!"-1".equals(authToken.CODE()) && !"admin".equals(fromToken.getUserName())) {
                    List<String> list = redisCache.getCacheObject(RedisConstants.LOGIN_MENU_CODE + fromToken.getUserName());
                    if (!hasPermissions(list, authToken.CODE())) {
                        throw new BaseException("管理员没有操作权限");
                    }
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

    private boolean hasPermissions(List<String> permissions, String permission) {
        return permissions.contains(permission);
    }
}
