package com.ezcoins.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Gjing
 **/
@Configuration
//@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketStompConfig
        implements WebSocketMessageBrokerConfigurer {

    //这个bean的注册,用于扫描带有@ServerEndpoint的注解成为websocket  ,如果你使用外置的tomcat就不需要该配置文件
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter()
//    {
//        return new ServerEndpointExporter();
//    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry){
//        webSocketHandlerRegistry.addHandler(new WebSocketHandler(),"/demo" ).setAllowedOrigins("*");
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //setAllowedOrigins("*")是设置所有请求都可以访问，即允许跨域的问题，或者自己设置允许访问的域名。
        registry.addEndpoint("/gs-guide-websocket").withSockJS();
    }
}
