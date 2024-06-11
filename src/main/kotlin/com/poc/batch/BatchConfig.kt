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
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Profile("!populatedb")
@Configuration
class BatchConfig(
    private val entityManagerFactory: EntityManagerFactory,
    private val dataSource: DataSource,
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
        transactionManager: PlatformTransactionManager,
        jobRepository: JobRepository
    ): Step {
        return StepBuilder("step", jobRepository)
            .chunk<Person, Person>(100, transactionManager)
            .reader(reader)
            .processor(processor())
            .writer(writer)
            .listener(CustomChunkListener())
            .listener(CustomItemReadListener())
            .build()
    }

    @Bean
    fun itemReader(): JpaPagingItemReader<Person> {
        val query = "select p from Person p"

        return JpaPagingItemReaderBuilder<Person>()
            .name("reader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(query)
            .pageSize(1000)
            .build()
    }

    @Bean
    fun writer(): ItemWriter<Person> {
        val query = "UPDATE Person SET decision = :decision, has_been_processed = :hasBeenProcessed WHERE id = :id"

        return JdbcBatchItemWriterBuilder<Person>()
            .dataSource(dataSource)
            .sql(query)
            .beanMapped()
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
