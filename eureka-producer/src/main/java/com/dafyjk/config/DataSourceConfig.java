package com.dafyjk.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.dafyjk.sequence.StepSequenceFactory;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.dafyjk.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

	@Value("${ds.url}")
	private String url;
	
	@Value("${ds.username}")
	private String username;
	
	@Value("${ds.password}")
	private String password;
	
    @Primary
    @Bean(initMethod = "init", destroyMethod = "close")
    public DruidDataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(10);
        druidDataSource.setMaxActive(100);
        druidDataSource.setMaxWait(60000L);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000L);
        druidDataSource.setMinEvictableIdleTimeMillis(300000L);
        druidDataSource.setValidationQuery("SELECT 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        Properties properties = new Properties();
        properties.put("druid.stat.mergeSql", "true");
        properties.put("druid.stat.slowSqlMillis", "5000");
        druidDataSource.setConnectProperties(properties);
        return druidDataSource;
    }
    
    @Primary
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:config/mybatis.xml"));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:com/dafyjk/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(initMethod = "init")
    @DependsOn("dataSource")
    public StepSequenceFactory stepSequenceFactory(DataSource dataSource) throws Exception {
        StepSequenceFactory stepSequenceFactory = new StepSequenceFactory();
        stepSequenceFactory.setType("request_no");
        stepSequenceFactory.setStep(1);
        stepSequenceFactory.setFormat("3{############}");
        stepSequenceFactory.setDataSource(dataSource);
        return stepSequenceFactory;
    }
}
