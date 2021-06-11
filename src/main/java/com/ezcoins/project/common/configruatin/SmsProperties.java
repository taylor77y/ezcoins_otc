package com.ezcoins.project.common.configruatin;

import lombok.Data;

/**
 * @Author: WangLei
 * @Email: 1044508403@qq.com
 * @Description:
 * @Date:2021/6/7 19:02
 * @Version:1.0
 */
@Data
@Deprecated
public class SmsProperties {
    private  String un;
    private  String pwd;
    private  Integer type;
    private  String agreedterm;
    private  String sendid;
}
