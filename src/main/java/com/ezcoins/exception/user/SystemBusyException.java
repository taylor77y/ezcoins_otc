package com.ezcoins.exception.user;

/**
 * 系统繁忙
 * 
 */
public class SystemBusyException extends UserException
{
    private static final long serialVersionUID = 1L;

    public SystemBusyException()
    {
        super("system.busy", null);
    }
}
