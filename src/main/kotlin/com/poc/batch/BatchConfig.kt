package com.poc.batch

import com.poc.batch.listeners.CustomChunkListener
import com.poc.domain.Person
import com.poc.domain.RawData
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.partition.support.Partitioner
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.task.TaskExecutor
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.Future

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
        processor: ItemProcessor<RawData, Future<Person>>,
        writer: ItemWriter<Future<Person>>,
        @Qualifier("rawDataPartitioner") partitioner: Partitioner,
        @Qualifier("taskExecutorWorker") taskExecutor: TaskExecutor
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
        processor: ItemProcessor<RawData, Future<Person>>,
        writer: ItemWriter<Future<Person>>
    ): Step {
        return StepBuilder("workerStep", jobRepository)
            .chunk<RawData, Future<Person>>(10000, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .listener(CustomChunkListener())
            .build()
    }
}
