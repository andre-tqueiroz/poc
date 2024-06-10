package com.poc.batch

import com.poc.domain.Decision
import com.poc.domain.Person
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class BatchConfig {

    @Bean
    fun job(jobRepository: JobRepository, step: Step): Job {
        return JobBuilder("job", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    fun step(
        jobRepository: JobRepository,
        transactionManager: DataSourceTransactionManager,
        reader: JdbcPagingItemReader<Person>,
        writer: JdbcBatchItemWriter<Person>,
    ): Step {
        return StepBuilder("step", jobRepository)
            .chunk<Person, Person>(1000, transactionManager)
            .reader(reader)
            .processor(processor())
            .writer(writer)
            //.taskExecutor(taskExecutor())
            .build()
    }

    @Bean
    fun reader(dataSource: DataSource, queryProvider: PagingQueryProvider): JdbcPagingItemReader<Person> {
        val jdbcTemplate = JdbcTemplate(dataSource)
        return JdbcPagingItemReaderBuilder<Person>()
            .name("personItemReader")
            .dataSource(dataSource)
            .pageSize(1000)
            .queryProvider(queryProvider)
            .rowMapper(BeanPropertyRowMapper(Person::class.java))
            .build()
    }

    @Bean
    fun queryProvider(): SqlPagingQueryProviderFactoryBean {
        val provider = SqlPagingQueryProviderFactoryBean()

        provider.setSelectClause("select *")
        provider.setFromClause("from person")
        provider.setWhereClause("where has_been_processed = false")
        provider.setSortKey("id")

        return provider
    }

    fun processor(): ItemProcessor<Person, Person> {
        return ItemProcessor<Person, Person> { person ->
            if (person.age >= 18 && person.score >= 6 && person.income >= 5000.0) {
                person.decision = Decision.APPROVED
            } else {
                person.decision = Decision.REJECTED
            }
            person.hasBeenProcessed = true
            person
        }
    }

    @Bean
    fun writer(dataSource: DataSource): JdbcBatchItemWriter<Person> {
        return JdbcBatchItemWriterBuilder<Person>()
            .sql(
                """
                    UPDATE person SET (decision, has_been_processed) VALUES (:decision, :hasBeenProcessed)
                    WHERE id = :id
                """
            )
            .dataSource(dataSource)
            .beanMapped()
            .build()
    }

//    @Bean
//    fun taskExecutor(): TaskExecutor {
//        val executor = ThreadPoolTaskExecutor()
//        executor.corePoolSize = 10
//        executor.maxPoolSize = 10
//        executor.queueCapacity = 10
//        executor.setThreadNamePrefix("BatchThread-")
//        executor.initialize()
//        return executor
//    }
}
