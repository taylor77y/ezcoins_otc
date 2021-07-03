package com.ezcoins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.socket.client.WebSocketClient;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EzcoinsParentApplication  {
    public static void main(String[] args) {
        SpringApplication.run(EzcoinsParentApplication.class, args);
    }
//    @Value("${uri.protocol:ws://}")
//    String protocol;
//
//    @Value("${uri.host:api.huobi.pro}")
//    String host;
//
//    @Value("${uri.port:443}")
//    String port;
//
//    @Value("${uri.ao.path:/ws/v1}")
//    String aO;
//
//    @Value("${uri.market.path:/ws}")
//    String market;
//
//    @Value("${accessKey}")
//    String accessKey;
//
//    @Value("${secretKey}")
//    String secretKey;
//
//    @Autowired
//    Client client;
//
//    @PostConstruct
//    private void init() {
//        ExecutorService executorService = Executors.newFixedThreadPool(50000);
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//
//                    URI uri = new URI(protocol + host + ":" + port + market);
//                    System.out.println(protocol + host + ":" + port + market + "  1");
//                    System.out.println(uri.getHost() + uri.getPath());
//                    WebSocketClient ws = new WebSocketAccountsAndOrders(uri, accessKey, secretKey);
//
//                    client.connect(ws);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        });
//
//    }
}