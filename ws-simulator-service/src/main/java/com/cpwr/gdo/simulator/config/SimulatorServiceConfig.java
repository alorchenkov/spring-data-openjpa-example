package com.cpwr.gdo.simulator.config;

import javax.sql.DataSource;

import com.cpwr.gdo.simulator.service.AuthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.cpwr.gdo.simulator.service.auditing.AuditorStringAware;

//@Configuration
@ImportResource({ "classpath:test-applicationContext.xml", "classpath:test-securityContext.xml" })
@ComponentScan(basePackages = { "com.cpwr.gdo.simulator","com.cpwr.gdo.simulator.service" } )
@EnableJpaRepositories("com.cpwr.gdo.simulator.dao.repository")
@EnableTransactionManagement
// @PropertySource({ "classpath:rest.properties", "classpath:web.properties" })
public class SimulatorServiceConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private Database databaseType;

    @Bean
    public AuditorStringAware auditorStringAware() {
        return new AuditorStringAware();

    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.cpwr.gdo.simulator");
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter() {
            {
                setShowSql(true);
                setGenerateDdl(true);
                setDatabase(databaseType);
            }
        };
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        transactionManager.setJpaDialect(new HibernateJpaDialect());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public StandardPasswordEncoder encoder() {
        return new StandardPasswordEncoder();
    }

    @Bean (name = "authService")
    public AuthDetailsService authService() {
        return new AuthDetailsService();
    }

}
