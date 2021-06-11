package com.ezcoins.base;

import com.ezcoins.security.util.SecurityUtils;
import com.ezcoins.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/4 16:24
 * @Version:1.0
 */
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }
    /**
     * 获取用户ID
     */
    protected String getUserId(){
        return SecurityUtils.getUserId();
    }

    /**
     * 获取登录用户信息
     */
    protected String getUserName(){
        return SecurityUtils.getUsername();
    }

}
