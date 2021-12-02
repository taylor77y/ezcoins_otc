package com.ezcoins.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class TransactionManagerConfig {

    @Bean
    public  PlatformTransactionManager transactionManager1(EntityManagerFactory entityManagerFactory) {
        return new  JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public PlatformTransactionManager transactionManager2(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
