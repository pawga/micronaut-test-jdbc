package com.pawga

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity("products")
data class Product(
    @Id // <2>
    val id: Long,
    val code: String,
    val name: String
)
