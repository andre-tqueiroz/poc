package com.poc.batch

import com.poc.domain.Decision
import com.poc.domain.Person
import com.poc.domain.RawData
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
    private val transactionManager: PlatformTransactionManager,
    private val dataSource: DataSource,
    private val jobRepository: JobRepository,
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
        reader: ItemReader<RawData>,
        processor: ItemProcessor<RawData, Person>,
        writer: ItemWriter<Person>
    ): Step {
        return StepBuilder("step", jobRepository)
            .chunk<RawData, Person>(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(CustomChunkListener())
            .listener(CustomItemReadListener())
            .build()
    }

    @Bean
    fun reader(): JpaPagingItemReader<RawData> {
        val query = "select rd from RawData rd"

        return JpaPagingItemReaderBuilder<RawData>()
            .name("reader")
            .entityManagerFactory(entityManagerFactory)
            .queryString(query)
            .build()
    }

    @Bean
    fun writer(): ItemWriter<Person> {
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
    fun processor(): ItemProcessor<RawData, Person> {
        return ItemProcessor<RawData, Person> { raw ->
            val person = Person(
                cpf = raw.cpf,
                name = raw.name,
                age = raw.age,
                score = raw.score,
                income = raw.income
            )
            if (person.age >= 18 && person.score >= 6 && person.income >= 5000.0) {
                person.decision = Decision.APPROVED.name
            } else {
                person.decision = Decision.REJECTED.name
            }
            person
        }
    }
}
