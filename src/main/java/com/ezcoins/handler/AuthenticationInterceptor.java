package com.ezcoins.handler;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.IgnoreUserToken;
import com.ezcoins.base.BaseException;
import com.ezcoins.constant.UserConstants;
import com.ezcoins.constant.enums.LoginType;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CheckException;
import com.ezcoins.exception.jwt.TokenException;
import com.ezcoins.project.acl.entity.AclUser;
import com.ezcoins.project.acl.service.AclUserService;
import com.ezcoins.project.consumer.entity.EzUser;
import com.ezcoins.project.consumer.service.EzUserService;
import com.ezcoins.security.util.IJWTInfo;
import com.ezcoins.security.util.JWTHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        IgnoreUserToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreUserToken.class);
        if (annotation != null) {
            return true;
        }
        Method method = handlerMethod.getMethod();
        AuthToken authToken = method.getAnnotation(AuthToken.class);
        if (authToken != null) {
            String token = jwtHelper.getToken(request);
            CheckException.checkToken(StringUtils.isEmpty(token), () -> {
                log.error("token失效，请重新登录");
            });
            //获取token
            if (StringUtils.isEmpty(token)) {
                throw new TokenException();
            }
            //解析token
            IJWTInfo fromToken = jwtHelper.getInfoFromToken(token);
            log.info("用户{}", fromToken);

            CheckException.checkToken(fromToken == null, () -> {
                log.error("token失效，请重新登录");
            });

            //根据token存储的值，redis判断是否失效
            boolean checkToken = jwtHelper.verifyToken(fromToken);

            CheckException.checkToken(!checkToken, () -> {
                log.error("token失效，请重新登录");
            });

            ContextHandler.setUserId(fromToken.getUserId());
            ContextHandler.setUserName(fromToken.getUserName());
            ContextHandler.setUserType(fromToken.getUserType());
            //APP权限
            if (fromToken.getUserType().equals(LoginType.APP.getType())) {
                EzUser user = null;
                if (authToken.advertisingStatus()) {
                    user = userService.getById(fromToken.getUserId());
                    CheckException.checkToken(user == null, () -> {
                        log.error("登录已失效，请重新登录");
                    });
                    CheckException.check("1".equals(user.getLevel()), "请先进行高级认证", () -> {
                        log.error("请先进行高级认证");
                    });
                }
                if (authToken.kyc()) {
                    if (user == null) {
                        user = userService.getById(fromToken.getUserId());
                        CheckException.checkToken(user == null, () -> {
                            log.error("登录已失效，请重新登录");
                        });
                    }
                    CheckException.check("1".equals(user.getKycStatus()), "请先完成实名认证");
                }

                if (authToken.status()) {
                    if (user == null) {
                        user = userService.getById(fromToken.getUserId());
                        CheckException.checkToken(user == null, () -> {
                            log.error("登录已失效，请重新登录");
                        });
                    }
                    CheckException.check("1".equals(user.getStatus()), "用户被禁止");
                }
            } else if (fromToken.getUserType().equals(LoginType.WEB.getType())) {
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
