package com.poc.batch

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class BdPartitionerConfig {

    @Bean
    fun rawDataPartitioner(
        dataSource: DataSource
    ): ColumnRangePartitioner {
        val partitioner = ColumnRangePartitioner()
        partitioner.setTable("raw_data")
        partitioner.setColumn("id")
        partitioner.setDataSource(dataSource)
        return partitioner
    }
}