package com.pawga

import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.Connection

@MicronautTest(startApplication = false) // <1>
@Sql(scripts = ["classpath:sql/init-db-2.sql", "classpath:sql/seed-data-2.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class DictionaryRepositoryTest {
    @Inject
    lateinit var connection: Connection
    @Inject
    lateinit var resourceLoader: ResourceLoader

    @Inject
    lateinit var dictionaryRepository: DictionaryRepository

    @Test
    fun shouldGetAllProducts() {
        runBlocking {
            launch(Dispatchers.IO) {
                val size = dictionaryRepository.findAll().count()
                Assertions.assertEquals(4, size)
            }
        }
    }

    @Test
    fun shouldNotCreateAProductWithDuplicateCode() {
        val product = DictionaryDb(7, "Источник дохода")
        runBlocking {
            launch(Dispatchers.IO) {
                //val result = dictionaryRepository.save(product);
                Assertions.assertDoesNotThrow {
                    launch(Dispatchers.IO) {
                        dictionaryRepository.save(product)
                    }
                }
                val optionalProduct = dictionaryRepository.findById(7)
                Assertions.assertTrue(optionalProduct == null)
            }
        }
    }
}
