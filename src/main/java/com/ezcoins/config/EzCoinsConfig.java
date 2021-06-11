package com.ezcoins.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author Administrator
 */
@Component
@ConfigurationProperties(prefix = "ezcoins")
public class EzCoinsConfig
{
    
    private String name;

    
    private static String profile;

    
    private static boolean addressEnabled;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        EzCoinsConfig.profile = profile;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        EzCoinsConfig.addressEnabled = addressEnabled;
    }

    
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }
}