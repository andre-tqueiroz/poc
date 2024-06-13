package com.poc.batch

import com.poc.batch.listeners.CustomChunkListener
import com.poc.domain.Decision
import com.poc.domain.Person
import com.poc.domain.RawData
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.partition.support.Partitioner
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.task.TaskExecutor
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Profile("!populatedb")
@Configuration
class BatchConfig(
    private val transactionManager: PlatformTransactionManager,
    private val jobRepository: JobRepository
) {

    @Bean
    fun job(
        jobRepository: JobRepository,
        @Qualifier("rawDataStepManager") stepManager: Step
    ): Job {
        return JobBuilder("job", jobRepository)
            .start(stepManager)
            .incrementer(RunIdIncrementer())
            .build()
    }

    @Bean
    fun rawDataStepManager(
        reader: ItemReader<RawData>,
        processor: ItemProcessor<RawData, Person>,
        writer: ItemWriter<Person>,
        @Qualifier("rawDataPartitioner") partitioner: Partitioner,
        taskExecutor: TaskExecutor
    ): Step {
        return StepBuilder("rawDataStepManager", jobRepository)
            .partitioner("rawDataStepManager", partitioner)
            .taskExecutor(taskExecutor)
            .gridSize(10)
            .step(rawDataStep(reader, processor, writer))
            .build()
    }

    private fun rawDataStep(
        reader: ItemReader<RawData>,
        processor: ItemProcessor<RawData, Person>,
        writer: ItemWriter<Person>
    ): Step {
        return StepBuilder("workerStep", jobRepository)
            .chunk<RawData, Person>(10000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(CustomChunkListener())
            .build()
    }
}
