package com.poc.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "person")
data class Person(

    @Id
    val id: UUID = UUID.randomUUID(),

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
