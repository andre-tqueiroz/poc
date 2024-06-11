package com.poc.batch

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig(
    @Value("\${spring.datasource.url}")
    private val url: String,
    @Value("\${spring.datasource.username}")
    private val username: String,
    @Value("\${spring.datasource.password}")
    private val password: String
) {

    @Bean
    fun customDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(url)
            .username(username)
            .password(password)
            .build()
    }

    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

}