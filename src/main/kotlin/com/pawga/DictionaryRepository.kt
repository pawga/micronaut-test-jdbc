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

    // <2>
    fun save(dictionary: DictionaryDb): DictionaryDb {
        return createProductIfNotExists(dictionary.name)
    }

    @Query(
        value = "insert into dictionary(name) values(:name) ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    abstract fun createProductIfNotExists(name: String): DictionaryDb

//    abstract fun save(
//        dictionary: DictionaryDb,
//    ): DictionaryDb
//
//    @Transactional
//    open fun saveWithException(
//        dictionary: DictionaryDb,
//    ): DictionaryDb {
//        save(dictionary)
//        throw DataAccessException("test exception")
//    }
}
