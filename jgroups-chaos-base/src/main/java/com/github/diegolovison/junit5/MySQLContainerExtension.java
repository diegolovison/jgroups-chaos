package com.github.diegolovison.junit5;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLContainerExtension implements BeforeEachCallback, AfterEachCallback {

   private static final Logger log = LoggerFactory.getLogger(MySQLContainerExtension.class);

   public static final MySqlExtensionBuilder builder() {
      return new MySqlExtensionBuilder();
   }

   private MySQLContainer mySQLContainer;
   private DataSource dataSource;

   public DataSource getDataSource() {
      return dataSource;
   }

   public String getJdbcUrl() {
      return mySQLContainer.getJdbcUrl();
   }

   @Override
   public void beforeEach(ExtensionContext extensionContext) throws Exception {
      if (mySQLContainer == null) {
         mySQLContainer = new MySQLContainer();
         mySQLContainer.withLogConsumer(new Slf4jLogConsumer(log));
         mySQLContainer.start();

         HikariConfig hikariConfig = new HikariConfig();
         hikariConfig.setJdbcUrl(mySQLContainer.getJdbcUrl());
         hikariConfig.setUsername(mySQLContainer.getUsername());
         hikariConfig.setPassword(mySQLContainer.getPassword());
         hikariConfig.setDriverClassName(mySQLContainer.getDriverClassName());

         this.dataSource = new HikariDataSource(hikariConfig);
      }
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      if (dataSource != null) {
         ((HikariDataSource) dataSource).close();
      }
      if (mySQLContainer != null) {
         mySQLContainer.close();
      }
   }

   public static class MySqlExtensionBuilder {

      public MySQLContainerExtension build() {
         MySQLContainerExtension extension = new MySQLContainerExtension();
         return extension;
      }
   }
}
