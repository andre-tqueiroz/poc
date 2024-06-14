package com.poc.batch.writers

import com.poc.domain.Person
import org.springframework.batch.integration.async.AsyncItemWriter
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Future
import javax.sql.DataSource

@Configuration
class PersonWriter {

    @Bean
    fun writer(
        dataSource: DataSource
    ): ItemWriter<Person> {
        val query = """
                INSERT INTO Person (id, cpf, name, age, score, income, created_at, decision)
                VALUES (:id, :cpf, :name, :age, :score, :income, :createdAt, :decision)
            """

        return JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql(query)
            .beanMapped()
            .build()
    }

    @Bean
    fun asyncWriter(
        writer: ItemWriter<Person>
    ): ItemWriter<Future<Person>> {
        val asyncWriter = AsyncItemWriter<Person>()
        asyncWriter.setDelegate(writer)

        return asyncWriter
    }
}