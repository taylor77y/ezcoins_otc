package com.ezcoins.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @Author:
 * @Email:
 * @Description:
 * @Date:2021/5/4 10:06
 * @Version:1.0
 */
@Data
public class NotifyMessage {
    String userId;
    Object data;

    public NotifyMessage(String userId,Object data) {
        this.userId = userId;
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this.data);
    }

}
