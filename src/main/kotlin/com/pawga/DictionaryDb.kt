package com.pawga

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable

/**
 * Created by sivannikov
 */

@Serdeable
@MappedEntity(value = "dictionary")
data class DictionaryDb(
    @Id
    val id: Long? = null,
    val name: String,
) {
    constructor(name: String) : this(id = null, name = name)
}
