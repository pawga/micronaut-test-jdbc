package com.pawga

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.jpa.kotlin.CoroutineJpaSpecificationExecutor
import io.micronaut.data.repository.kotlin.CoroutineCrudRepository

/**
 * Created by sivannikov on 11.12.2023 13:18
 */

@JdbcRepository(dialect = Dialect.POSTGRES)
abstract class DictionaryRepository :
    CoroutineCrudRepository<DictionaryDb, Long>,
    CoroutineJpaSpecificationExecutor<DictionaryDb> {

    suspend fun create(dictionary: DictionaryDb): Long {
        return if (dictionary.id != null) {
            save(dictionary).id ?: 0L
        } else {
            createProductIfNotExists(dictionary.name)
        }
    }

    @Query("insert into dictionary(name) values(:name) RETURNING id;")
    abstract fun createProductIfNotExists(name: String): Long
}
