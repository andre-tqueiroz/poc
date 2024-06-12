package com.poc.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*
import kotlin.math.round
import kotlin.random.Random

@Entity
@Table(name = "raw_data")
data class RawData(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val cpf: String = (10000000000 until 99999999999).random().toString(),

    val name: String = "Random Name ${(1 until 99999999999).random()}",

    val age: Int = (1 until 99).random(),

    val score: Int = (0 until 10).random(),

    val income: Double = round(Random.nextDouble(1000.0, 30000.0))

)