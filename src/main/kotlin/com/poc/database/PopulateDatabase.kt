package com.poc.database

import com.poc.domain.Person
import com.poc.repository.PersonRepository
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("populatedb")
@Configuration
class PopulateDatabase(
    private val personRepository: PersonRepository
) {

    @PostConstruct
    fun populateDatabase() {
        println("populating database...")
        for (i in 1..1000) {
            personRepository.save(Person())
        }
        println("Done!")
    }

}
