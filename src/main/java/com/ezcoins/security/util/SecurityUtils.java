package com.ezcoins.security.util;

import com.ezcoins.constant.interf.HttpStatus;
import com.ezcoins.context.ContextHandler;
import com.ezcoins.exception.CustomException;


/**
 * @author Administrator
 */
public class SecurityUtils{
    public static String getUsername(){
        try {
            return ContextHandler.getUserName();
        }catch (Exception e){
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }
    public static String getUserId(){
        try {
            return ContextHandler.getUserId();
        }catch (Exception e){
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getUserType(){
        try {
            return ContextHandler.getUserType();
        }catch (Exception e){
            throw new CustomException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }
}
