package com.poc.database

import com.poc.domain.RawData
import com.poc.repository.RawDataRepository
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("populatedb")
@Configuration
class PopulateDatabase(
    private val rawDataRepository: RawDataRepository
) {

    @PostConstruct
    fun populateDatabase() {
        println("populating database...")
        for (i in 1..16) {
            try {
                rawDataRepository.save(RawData())
            } catch (ex: RuntimeException) {
                println(ex.localizedMessage)
            }
        }
        println("Done!")
    }

}
