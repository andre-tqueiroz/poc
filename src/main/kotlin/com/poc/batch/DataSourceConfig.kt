package com.poc.batch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun datasource(): DataSource {
        return DataSourceBuilder.create().build()
    }

//    @Bean
//    fun transactionManager(datasource: DataSource): DataSourceTransactionManager {
//        return DataSourceTransactionManager(datasource)
//    }

}