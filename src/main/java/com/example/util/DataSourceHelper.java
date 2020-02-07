package com.example.util;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceHelper {

	static final String prefix = "netclient6";

	public static DataSource createAndConfigureDataSource(String tenantId) {

		String cpName;
		String dbName;
		if (StringUtils.isBlank(tenantId)) {
			dbName = prefix;
			cpName = "none-tenanat-id";
		} else {
			dbName = prefix + "_" + tenantId;
			cpName = tenantId;
		}

		String url = String.format("jdbc:mariadb://localhost:40001/%s?useUnicode=true&amp;characterEncoding=UTF-8", dbName);

		HikariDataSource ds = new HikariDataSource();
		ds.setUsername("root");
		ds.setPassword("root");
		ds.setJdbcUrl(url);
		ds.setDriverClassName("org.mariadb.jdbc.Driver");

		ds.setConnectionTimeout(20000);
		ds.setMinimumIdle(10);
		ds.setMaximumPoolSize(20);
		ds.setIdleTimeout(300000);
		ds.setConnectionTimeout(20000);

		String tenantConnectionPoolName = cpName + "-connection-pool";
		ds.setPoolName(tenantConnectionPoolName);
		return ds;
	}

}
