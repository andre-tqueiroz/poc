package com.poc.batch


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
class TaskExecutorConfig {

    @Bean
    fun taskExecutorWorker(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 10
        executor.queueCapacity = 10
        executor.maxPoolSize = 10
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.setThreadNamePrefix("PartitionLocal-")

        return executor
    }

    @Bean
    fun taskExecutorAsync(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 100
        executor.queueCapacity = 100
        executor.maxPoolSize = 100
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.setThreadNamePrefix("Async-")

        return executor
    }
}