package com.example.Blasira_Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource; // Keep import if needed for other methods, otherwise remove
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    // private final DataSource dataSource; // Removed

    // public JpaConfig(DataSource dataSource) { // Removed constructor
    //     this.dataSource = dataSource;
    // }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) { // Inject DataSource here
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.example.Blasira_Backend.model"); // Adjust to your entity package
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(hibernateProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        // Set these based on your application.properties or environment
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none"); // Flyway manages schema
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); // Or auto-detect with newer Hibernate
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        return hibernateProperties;
    }
}
