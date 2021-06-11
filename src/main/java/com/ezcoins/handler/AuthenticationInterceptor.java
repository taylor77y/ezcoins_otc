package com.ezcoins.handler;

import com.ezcoins.aspectj.lang.annotation.AuthToken;
import com.ezcoins.aspectj.lang.annotation.IgnoreUserToken;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.jwt.TokenException;
import com.ezcoins.security.util.IJWTInfo;
import com.ezcoins.security.util.JWTHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {return true;}
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        IgnoreUserToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreUserToken.class);
        if (annotation!=null){
            return true;
        }
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

            log.info("用户{}", fromToken);
            if (fromToken == null) {
                throw new TokenException();
            }

            //根据token存储的值，redis判断是否失效
            boolean checkToken = jwtHelper.verifyToken(fromToken);
            if (!checkToken) {
                log.info("请求路径{} token为空",request.getPathInfo());
                throw new TokenException();
            }
            ContextHandler.setUserId(fromToken.getUserId());
            ContextHandler.setUserName(fromToken.getUserName());
            ContextHandler.setUserType(fromToken.getUserType());


           //存储数据

//
//            //APP权限
//            if (userLogin.getUserType().equals(LoginType.APP.getType())) {
//                PingUser user = null;
//                if (authToken.active()) {
//                    user = userMapper.selectById(userLogin.getUserId());
//                    CheckException.checkToken(user == null);
//                    CheckException.check(user.getActive().equals(UserConstant.OpenOrClose.ZERO), "用户未激活");
//                }
//
//                if (authToken.kyc()) {
//                    if (user == null) {
//                        user = userMapper.selectById(userLogin.getUserId());
//                        CheckException.checkToken(user == null);
//                        CheckException.check(!user.getKycStatus().equals(UserConstant.KycStatus.adopt), "实名认证未通过");
//                    }
//                }
//
//                if (authToken.status()) {
//                    if (user == null) {
//                        user = userMapper.selectById(userLogin.getUserId());
//                        CheckException.checkToken(user == null);
//                        CheckException.check(user.getStatus().equals(UserConstant.OpenOrClose.ZERO), "用户被禁止");
//                    }
//                }
//            } else if (userLogin.getUserType().equals(LoginType.WEB.getType())) {
//                //WEB权限
//                PingAdmin admin;
//                if (authToken.status()) {
//                    admin = adminMapper.selectById(userLogin.getUserId());
//                    CheckException.checkToken(admin == null);
//                    CheckException.check(admin.getStatus().equals(UserConstant.OpenOrClose.ZERO), "用户被禁止");
//                }
//            } else {
//                //SHOP权限
//                PingShopUser shopUser;
//                if (authToken.status()) {
//                    shopUser = shopUserMapper.selectById(userLogin.getUserId());
//                    CheckException.checkToken(shopUser == null);
//                    CheckException.check(shopUser.getStatus().equals(UserConstant.OpenOrClose.ZERO), "用户被禁止");
//                }
//            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
