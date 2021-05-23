package com.barbariania.awsinfo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {
  @Value("${sql.driverClassName}")
  private String driverClassName;
  @Value("${sql.url}")
  private String dbUrl;
  @Value("${sql.schema}")
  private String schema;
  @Value("${sql.username}")
  private String dbUsername;
  @Value("${sql.password}")
  private String dbPassword;

  @Bean
  public DriverManagerDataSource dataSource() throws ClassNotFoundException {
    Class.forName(driverClassName);
    DriverManagerDataSource dataSource = new DriverManagerDataSource(dbUrl, dbUsername, dbPassword);
    dataSource.setDriverClassName(driverClassName);
//    dataSource.setSchema(schema); //does not affect workability
    return dataSource;
  }
}
