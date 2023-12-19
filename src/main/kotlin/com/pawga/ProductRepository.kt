package com.pawga

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
abstract class ProductRepository : CrudRepository<Product, Long> {
    fun createProductIfNotExists(product: Product) {
        createProductIfNotExists(product.id, product.code, product.name)
    }

    @Query(
        value = "insert into products(id, code, name) values(:id, :code, :name) ON CONFLICT DO NOTHING",
        nativeQuery = true
    )
    abstract fun createProductIfNotExists(id: Long, code: String, name: String)
}
