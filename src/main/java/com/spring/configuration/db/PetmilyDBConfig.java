package com.spring.configuration.db;

import com.spring.util.ApplicationYamlRead;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "petmilyEntityManager",
        transactionManagerRef = "petmilyTransactionManager",
        basePackages = "com.spring.domain"
)
public class PetmilyDBConfig {
    @Autowired
    private ApplicationYamlRead applicationYamlRead;

    @Primary
    @Bean
    public DataSource petmilylDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(applicationYamlRead.getPetmily_driver_class_name());
        hikariDataSource.setJdbcUrl(applicationYamlRead.getPetmily_url());
        hikariDataSource.setUsername(applicationYamlRead.getPetmily_username());
        hikariDataSource.setPassword(applicationYamlRead.getPetmily_password());
        System.out.println("잘되나 test");
        return hikariDataSource;
    }

    @Primary
    @Bean(name = "petmilyEntityManager")
    public LocalContainerEntityManagerFactoryBean petmilyEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = new HashMap<String, Object>();
        // yml이나 properties에서도 써줄 수 있지만 여러 디비를 관리하다보면 밑에와같이 쓸 수 있습니다.
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("show-sql", "true");
        properties.put("format_sql", "true");
        return builder
                .dataSource(petmilylDataSource())
                .properties(properties)
                //.packages(TestModel.class)
                .packages("com.spring.domain")
                .persistenceUnit("petmily")
                .build();
    }

    @Primary
    @Bean(name = "petmilyTransactionManager")
    public PlatformTransactionManager petmilyTransactionManager(@Qualifier("petmilyEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map hibernateProperties() {
        Resource resource = new ClassPathResource("hibernate.properties");

        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);

            return properties.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue())
                    );
        } catch (IOException e) {
            return new HashMap();
        }
    }
}