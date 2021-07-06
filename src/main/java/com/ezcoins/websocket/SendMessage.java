package com.ezcoins.websocket;

import lombok.Data;

@Data
public class SendMessage {
    private String topic;
    private Object data;
}
