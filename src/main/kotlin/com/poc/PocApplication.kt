package com.poc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PocApplication

fun main(args: Array<String>) {
    runApplication<PocApplication>() {
        if (args.isNotEmpty() && args[0] == "populatedb") {
            this.setAdditionalProfiles("populatedb")
        }
    }
}

