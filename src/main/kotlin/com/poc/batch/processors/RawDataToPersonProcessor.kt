package com.poc.batch.processors

import com.poc.domain.Decision
import com.poc.domain.Person
import com.poc.domain.RawData
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
            if (person.age >= 18 && person.score >= 6 && person.income >= 5000.0) {
                person.decision = Decision.APPROVED.name
            } else {
                person.decision = Decision.REJECTED.name
            }
            person
        }
    }
}