package com.pawga

import io.micronaut.core.annotation.NonNull
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.sql.Connection
import java.util.Map

@MicronautTest(startApplication = false) // <1>
@Testcontainers(disabledWithoutDocker = true) // <2>
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // <3>
@Sql(scripts = ["classpath:sql/seed-data.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class ProductRepositoryTest : TestPropertyProvider {
    override fun getProperties(): @NonNull MutableMap<String, String>? { // <5>
        if (!postgres.isRunning) {
            postgres.start()
        }
        return Map.of(
            "datasources.default.driver-class-name", "org.postgresql.Driver",
            "datasources.default.url", postgres.jdbcUrl,
            "datasources.default.username", postgres.username,
            "datasources.default.password", postgres.password
        )
    }

    @Inject
    var connection: Connection? = null

    @Inject
    var resourceLoader: ResourceLoader? = null

    @Inject
    var productRepository: ProductRepository? = null

    @Test
    fun shouldGetAllProducts() {
        val products = productRepository!!.findAll()
        Assertions.assertEquals(2, products.size)
    }

    @Test
    fun shouldNotCreateAProductWithDuplicateCode() {
        val product = Product(3L, "p101", "Test Product")
        productRepository!!.createProductIfNotExists(product)
        val optionalProduct = productRepository!!.findById(product.id)
        Assertions.assertTrue(optionalProduct.isEmpty)
    }

    companion object {
        // <5>
        @Container
        var postgres: PostgreSQLContainer<*> = PostgreSQLContainer(
            "postgres:15.2-alpine"
        ).withCopyFileToContainer(
            MountableFile.forClasspathResource("sql/init-db.sql"),
            "/docker-entrypoint-initdb.d/init-db.sql"
        )
    }
}
