package com.poc.batch

import com.poc.domain.Decision
import com.poc.domain.Person
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class BatchConfig(
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
) {

    @Bean
    fun job(jobRepository: JobRepository, step: Step): Job {
        return JobBuilder("job", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    fun step(
        reader: ItemReader<Person>,
        writer: ItemWriter<Person>,
        jobRepository: JobRepository
    ): Step {
        return StepBuilder("step", jobRepository)
            .chunk<Person, Person>(10, transactionManager)
            .reader(reader)
            .processor(processor())
            .writer(writer)
            .build()
    }

    @Bean
    fun itemReader(): JpaPagingItemReader<Person> {
        return JpaPagingItemReaderBuilder<Person>()
            .name("reader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("select p from Person p")
            .pageSize(1000)
            .build()
    }

    @Bean
    fun writer(dataSource: DataSource): ItemWriter<Person> {
        val query = "UPDATE Person SET decision = :decision, has_been_processed = :hasBeenProcessed WHERE id = :id"

        return JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql(query)
            .itemSqlParameterSourceProvider(BeanPropertyItemSqlParameterSourceProvider())
            .build()
    }

    fun processor(): ItemProcessor<Person, Person> {
        return ItemProcessor<Person, Person> { person ->
            if (person.age >= 18 && person.score >= 6 && person.income >= 5000.0) {
                person.decision = Decision.APPROVED.name
            } else {
                person.decision = Decision.REJECTED.name
            }
            person.hasBeenProcessed = true
            person
        }
    }
}
