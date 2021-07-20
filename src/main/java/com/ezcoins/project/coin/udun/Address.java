package com.ezcoins.project.coin.udun;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * {"code":200,"msg":"操作成功","time":1626317509,"data":[{"id":3,"icon":"","name":"泰达币","address":"0x67ad39022a1115cb1e645bb9f52fa9e1a7199aa1","code":"USDT"}]}
 * @author Administrator
 */
@Data
public class Address {
    private Integer id;
    private String name;
    private String address;
    private String code;
    public static Address parse(String json){
        return JSON.parseObject(json,Address.class);
    }
}
