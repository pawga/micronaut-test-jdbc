package com.pawga

import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

@MicronautTest(startApplication = false) // <1>
@Sql(scripts = ["classpath:sql/init-db.sql", "classpath:sql/seed-data.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class ProductRepositoryTest {
    @Inject
    lateinit var connection: Connection
    @Inject
    lateinit var resourceLoader: ResourceLoader

    @Inject
    lateinit var productRepository: ProductRepository

    @Test
    fun shouldGetAllProducts() {
        val products = productRepository!!.findAll()
        Assertions.assertEquals(2, products.size)
    }

    @Test
    fun shouldNotCreateAProductWithDuplicateCode() {
        val product = Product(3L, "p101", "Test Product")
        Assertions.assertDoesNotThrow { productRepository!!.createProductIfNotExists(product) }
        val optionalProduct = productRepository!!.findById(product.id)
        Assertions.assertTrue(optionalProduct.isEmpty)
    }
}
