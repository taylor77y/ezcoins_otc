package com.ezcoins.exception.user;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/21 16:27
 * @Version:1.0
 */
public class SecurityPasswordNotMatchException extends UserException{

    private static final long serialVersionUID = 1L;

    public SecurityPasswordNotMatchException(){
        super("安全密码错误", null);
    }
}
