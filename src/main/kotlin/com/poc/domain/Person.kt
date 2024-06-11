package com.poc.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*
import kotlin.math.round
import kotlin.random.Random

@Entity
@Table(name = "person")
data class Person(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val cpf: String = (10000000000 until 99999999999).random().toString(),

    val name: String = "Random Name ${(1 until 99999999999).random()} ",

    val age: Int = (1 until 99).random(),

    val score: Int = (0 until 10).random(),

    val income: Double = round(Random.nextDouble(1000.0, 30000.0)),

    var hasBeenProcessed: Boolean = false,

    val createdAt: LocalDateTime? = LocalDateTime.now(),

    //@Enumerated(EnumType.STRING)
    var decision: String = Decision.UNDEFINED.name

)
