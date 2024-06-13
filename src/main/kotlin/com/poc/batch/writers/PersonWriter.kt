package com.poc.batch.writers

import com.poc.domain.Person
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
}