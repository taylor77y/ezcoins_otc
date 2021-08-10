//package com.ezcoins.security.service;
//
//import com.ezcoins.constant.interf.RedisConstants;
//import com.ezcoins.context.ContextHandler;
//import com.ezcoins.redis.RedisCache;
//import com.ezcoins.security.util.JWTHelper;
//
//import com.ezcoins.utils.ServletUtils;
//import com.ezcoins.utils.StringUtils;
//import org.apache.poi.ss.formula.functions.T;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.token.TokenService;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
///**
// * 自定义权限
// *
// */
//@Service("ss")
//public class PermissionService{
//    /** 管理员角色权限标识 */
//    private static final String SUPER_ADMIN = "admin";
//    @Autowired
//    private RedisCache redisCache;
//
//    public boolean hasPermi(String permission){
//        if (StringUtils.isEmpty(permission)){
//            return false;
//        }
//        String userName = ContextHandler.getUserName();
//        if (StringUtils.isEmpty(userName)){
//            return false;
//        }
//        if (SUPER_ADMIN.equals(userName)){
//            return true;
//        }
//
//        List<String> list =redisCache.getCacheObject(RedisConstants.LOGIN_MENU_CODE + ContextHandler.getUserName());
//        if (CollectionUtils.isEmpty(list)){
//            return false;
//        }
//        return hasPermissions(list, permission);
//    }
//
//    private boolean hasPermissions(List<String> permissions, String permission) {
//        return permissions.contains(permission);
//    }
//
//}
