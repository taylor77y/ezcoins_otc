package com.ezcoins.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

//@Configuration
//@MapperScan(DataSourceConfig.PACKAGE)
//public class DataSourceConfig {
//
//    // 精确到 master 目录，以便跟其他数据源隔离
//    static final String PACKAGE = "com.ezcoins.project.**.mapper";
//    static final String MAPPER_LOCATION = "classpath*:mybatis/**/*Mapper.xml";
////    static final String MAPPER_LOCATION1 = "classpath:mybatis/*/*.xml,classpath*:**/mapper/*.xml";
//
//    @Value("${spring.datasource.druid.master.url}")
//    private String url;
//    @Value("${spring.datasource.druid.master.username}")
//    private String username;
//    @Value("${spring.datasource.druid.master.password}")
//    private String password;
//    @Value("${spring.datasource.driverClassName}")
//    private String driver;
//
//    @Bean(name = "dataSource")
//    public DataSource dataSource() {
//        //DruidDataSource dataSource = new DruidDataSource();//alibaba druid datasource
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driver);
//        dataSource.setUrl(url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//
//    /*@Bean
//    @ConfigurationProperties("spring.datasource.druid.master")
//    public DataSource masterDataSource(DruidProperties druidProperties)
//    {
//        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
//        return druidProperties.dataSource(dataSource);
//    }*/
//
//    @Bean(name = "transactionManager")
//    public DataSourceTransactionManager transactionManager() {
//        return new DataSourceTransactionManager(dataSource());
//    }
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
//            throws Exception {
//        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource);
//        sessionFactory.setTypeAliasesPackage("com.ezcoins.project.**.domain");    // 扫描Model
//        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
//                .getResources(DataSourceConfig.MAPPER_LOCATION));
//                    return sessionFactory.getObject();
//    }
//
//    /*@Bean
//    public PlatformTransactionManager platformTransactionManager(){
//
//        return new DataSourceTransactionManager(masterDataSource());
//    }*/
//}
