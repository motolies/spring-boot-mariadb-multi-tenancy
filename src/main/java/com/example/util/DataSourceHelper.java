package com.example.util;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

@Component
public class DataSourceHelper {
	
	static String prefix;

    @Value("${spring.datasource.database.name.prefix}")
    public void setPrefix(String prefixProperties) {
    	prefix = prefixProperties;
    }
	
    static String urlFormat;
    
    @Value("${spring.datasource.url}")
    public void setUrlFormat(String urlProperties) {
    	urlFormat = urlProperties;
    }	
    
    static String userName;
    
    @Value("${spring.datasource.username}")
    public void setUserName(String userNameProperties) {
    	userName = userNameProperties;
    }	
    
    static String passWord;
    
    @Value("${spring.datasource.password}")
    public void setPassWord(String passWordProperties) {
    	passWord = passWordProperties;
    }	
	
	public static DataSource createAndConfigureDataSource(String tenantId) {

		String dbName;
		if (StringUtils.isBlank(tenantId)) {
			dbName = prefix;
		} else {
			dbName = prefix + "_" + tenantId;
		}

		String url = String.format(urlFormat, dbName);

		HikariDataSource ds = new HikariDataSource();
		ds.setUsername(userName);
		ds.setPassword(passWord);
		ds.setJdbcUrl(url);
		ds.setDriverClassName("org.mariadb.jdbc.Driver");

		ds.setConnectionTimeout(30000);
		ds.setMinimumIdle(10);
		ds.setMaximumPoolSize(20);
		ds.setIdleTimeout(300000);
		ds.setConnectionTimeout(30000);
		ds.setLeakDetectionThreshold(5000);
		ds.setAutoCommit(true);
		ds.setMaxLifetime(1800000);
		ds.setConnectionTestQuery("select 1");

		String tenantConnectionPoolName = dbName + "-connection-pool";
		ds.setPoolName(tenantConnectionPoolName);
		return ds;
	}

}