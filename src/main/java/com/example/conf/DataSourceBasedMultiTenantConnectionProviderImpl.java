package com.example.conf;

import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.util.DataSourceHelper;

public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 1L;

	@Autowired
	private Map<String, DataSource> NxcDataSources = new TreeMap<>();

	@Override
	protected DataSource selectAnyDataSource() {
		return this.NxcDataSources.values().iterator().next();
	}

	@Override
	protected DataSource selectDataSource(String tenantId) {
		//tenant id 를 가지고 여기서 조회를 한다.
		if (!this.NxcDataSources.containsKey(tenantId)) {
			NxcDataSources.put(tenantId, DataSourceHelper.createAndConfigureDataSource(tenantId));
		}
		return this.NxcDataSources.get(tenantId);
	}

}
