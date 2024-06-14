package com.poc.batch.processors

import com.poc.domain.Decision
import com.poc.domain.Person
import com.poc.domain.RawData
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Future

@Configuration
class RawDataToPersonProcessor {

    @Bean
    fun toPersonProcessor(): ItemProcessor<RawData, Person> {
        return ItemProcessor<RawData, Person> { raw ->
            val person = Person(
                id = raw.id,
                cpf = raw.cpf,
                name = raw.name,
                age = raw.age,
                score = raw.score,
                income = raw.income
            )

            Thread.sleep(100)

            if (person.age >= 18 && person.score >= 6 && person.income >= 5000.0) {
                person.decision = Decision.APPROVED.name
            } else {
                person.decision = Decision.REJECTED.name
            }
//            println("After processing: ${person.id}. ${Thread.currentThread().name}")

            person
        }
    }

    @Bean
    fun asyncProcessor(
        @Qualifier("taskExecutorAsync") taskExecutor: TaskExecutor,
        processor: ItemProcessor<RawData, Person>
    ): ItemProcessor<RawData, Future<Person>> {
        val asyncProcessor = AsyncItemProcessor<RawData, Person>()
        asyncProcessor.setTaskExecutor(taskExecutor)
        asyncProcessor.setDelegate(processor)

        return asyncProcessor
    }
}