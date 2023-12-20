package com.pawga

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

@MicronautTest(startApplication = false) // <1>
@Property(
    name = "datasources.default.driver-class-name",
    value = "org.testcontainers.jdbc.ContainerDatabaseDriver"
) // <2>
@Property(
    name = "datasources.default.url",
    value = "jdbc:tc:postgresql:15.2-alpine:///db?TC_INITSCRIPT=sql/init-db.sql"
) // <3>
@Sql(scripts = ["classpath:sql/seed-data.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class ProductRepositoryWithJdbcUrlTest {
    @Inject
    var connection: Connection? = null

    @Inject
    var resourceLoader: ResourceLoader? = null

    @Test
    fun shouldGetAllProducts(productRepository: ProductRepository) {
        val products = productRepository.findAll()
        Assertions.assertEquals(2, products.size)
    }
}
