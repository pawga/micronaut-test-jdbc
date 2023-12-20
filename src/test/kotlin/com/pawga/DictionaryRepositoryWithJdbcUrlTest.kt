package com.pawga

import io.micronaut.context.annotation.Property
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.sql.Connection

@MicronautTest(startApplication = false)
@Property(
    name = "datasources.default.driver-class-name",
    value = "org.testcontainers.jdbc.ContainerDatabaseDriver"
)
@Property(
    name = "datasources.default.url",
    value = "jdbc:tc:postgresql:15.2-alpine:///db?TC_INITSCRIPT=sql/init-db-2.sql"
)
@Sql(scripts = ["classpath:sql/seed-data-2.sql"], phase = Sql.Phase.BEFORE_EACH)
class DictionaryRepositoryWithJdbcUrlTest {
    @Inject
    var connection: Connection? = null

    @Inject
    var resourceLoader: ResourceLoader? = null

    @Test
    fun `test Repository`(dictionaryRepository: DictionaryRepository) {

        // test size
        runBlocking {
            launch(Dispatchers.IO) {
                val size = dictionaryRepository.findAll().count()
                Assertions.assertEquals(4, size)
            }
        }

        // test uniq field
        val product = DictionaryDb("Источник дохода")
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    dictionaryRepository.save(product)
                } catch (e: Exception) {
                    log.debug("This is normal")
                }
            }
            val optionalProduct = dictionaryRepository.findById(1L)
            Assertions.assertTrue(optionalProduct != null)
        }

        // test create and update
        val dictionaryDb = DictionaryDb(NEW_NAME)
        runBlocking {
            val id = dictionaryRepository.create(dictionaryDb)
            val newDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(newDictionary != null)
            val updateRecord = DictionaryDb(id, UPDATE_NAME)
            dictionaryRepository.update(updateRecord)
            val findDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(findDictionary?.name == UPDATE_NAME)

            dictionaryRepository.deleteById(id)
            val deletedDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(deletedDictionary == null)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DictionaryRepositoryWithJdbcUrlTest::class.java)
        const val NEW_NAME = "Test value"
        const val UPDATE_NAME = "Changed value"
    }
}
