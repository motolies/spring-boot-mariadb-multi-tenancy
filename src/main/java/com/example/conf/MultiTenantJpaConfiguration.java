package com.example.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.example.entity.Order;
import com.example.util.DataSourceHelper;

@Configuration
@EnableConfigurationProperties({ JpaProperties.class })
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(basePackages = { "com.example.repository" }, transactionManagerRef = "transactionManager")
public class MultiTenantJpaConfiguration {

	// @Value 사용을 위한 더미
	@Autowired
	private DataSourceHelper dataSourceHelper;
	
	
	
	@Autowired
	private DataSourceProperties properties;

	@Autowired
	private JpaProperties jpaProperties;

	@Bean
	public MultiTenantConnectionProvider multiTenantConnectionProvider() {
		return new DataSourceBasedMultiTenantConnectionProviderImpl();
	}

	@Bean
	public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
		return new CurrentTenantIdentifierResolverImpl();
	}

	@Bean(name = "NxcDataSources")
	public Map<String, DataSource> NxcDataSources() {
		final String defaultTenantId = "";

		Map<String, DataSource> datasources = new HashMap<>();
		datasources.put(defaultTenantId, DataSourceHelper.createAndConfigureDataSource(defaultTenantId));

		return datasources;
	}

	@PersistenceContext
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(MultiTenantConnectionProvider multiTenantConnectionProvider,
			CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {

		Map<String, Object> hibernateProps = new LinkedHashMap<>();
		hibernateProps.putAll(this.jpaProperties.getProperties());
		hibernateProps.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
		hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

		// No dataSource is set to resulting entityManagerFactoryBean
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPackagesToScan(new String[] { Order.class.getPackage().getName() });
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		em.setJpaPropertyMap(hibernateProps);

		return em;
	}

	@Bean
	public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		return entityManagerFactoryBean.getObject();
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpa = new JpaTransactionManager();
		jpa.setEntityManagerFactory(entityManagerFactory);
		return jpa;
	}

}
