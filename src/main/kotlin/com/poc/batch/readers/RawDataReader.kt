package com.poc.batch.readers

import com.poc.batch.RawDataRowMapper
import com.poc.domain.RawData
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class RawDataReader {

    @StepScope
    @Bean
    fun reader(
        queryProvider: PagingQueryProvider,
        dataSource: DataSource
    ): JdbcPagingItemReader<RawData> {
        return JdbcPagingItemReaderBuilder<RawData>()
            .dataSource(dataSource)
            .name("reader")
            .queryProvider(queryProvider)
            .pageSize(10000)
            .rowMapper(RawDataRowMapper())
            .build()
    }

    @StepScope
    @Bean
    fun queryProvider(
        @Value("#{stepExecutionContext['minValue']}") minValue: Long,
        @Value("#{stepExecutionContext['maxValue']}") maxValue: Long,
        dataSource: DataSource
    ): SqlPagingQueryProviderFactoryBean {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setSelectClause("*")
        queryProvider.setFromClause("from raw_data")
        queryProvider.setWhereClause("where id >= $minValue and id <= $maxValue")
        queryProvider.setSortKey("id")
        queryProvider.setDataSource(dataSource)

        return queryProvider
    }

}