package com.poc.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "person")
data class Person(

    @Id
    val id: Long?,

    @Column(unique = true)
    val cpf: String,

    val name: String,

    val age: Int,

    val score: Int,

    val income: Double,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    //@Enumerated(EnumType.STRING)
    var decision: String = Decision.UNDEFINED.name

)
