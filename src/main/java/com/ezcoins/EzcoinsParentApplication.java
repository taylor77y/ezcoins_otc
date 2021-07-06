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
public class EzcoinsParentApplication {
    public static void main(String[] args) {
        SpringApplication.run(EzcoinsParentApplication.class, args);
    }
}