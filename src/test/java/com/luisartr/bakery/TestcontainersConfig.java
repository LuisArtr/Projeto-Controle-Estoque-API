package com.luisartr.bakery;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mssqlserver.MSSQLServerContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    MSSQLServerContainer sqlServer() {
        return new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2022-latest")
                .acceptLicense();
    }
}