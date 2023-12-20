package com.pawga

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.data.annotation.Relation.Cascade
import io.micronaut.serde.annotation.Serdeable

/**
 * Created by sivannikov
 */

@Serdeable
@MappedEntity(value = "dictionary")
data class DictionaryDb(
    @Id
//    @GeneratedValue
    val id: Long,
    val name: String,
)
