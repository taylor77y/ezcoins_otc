package com.ezcoins.constant.enums;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/5/31 16:45
 * @Version:1.0
 */
public enum FavFlag {

    FOLLOW("0", "关注"),UNFOLLOW("1", "取消关注");

    private final String code;
    private final String info;

    FavFlag(String code, String info)
    {
        this.code = code;
        this.info = info;
    }
    public String getCode()
    {
        return code;
    }
    public String getInfo()
    {
        return info;
    }


}
