package com.ezcoins.exception.user;

/**
 * 没有权限异常类
 * 
 */
public class NoPermissionException extends UserException
{
    private static final long serialVersionUID = 1L;

    public NoPermissionException()
    {
        super("system.beyond.authority", null);
    }
}
