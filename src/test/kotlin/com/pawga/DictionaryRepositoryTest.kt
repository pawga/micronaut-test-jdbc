package com.pawga

import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@MicronautTest(startApplication = false) // <1>
@Sql(scripts = ["classpath:sql/init-db-2.sql", "classpath:sql/seed-data-2.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class DictionaryRepositoryTest {
//    @Inject
//    lateinit var connection: Connection
//
//    @Inject
//    lateinit var resourceLoader: ResourceLoader

    @Inject
    lateinit var dictionaryRepository: DictionaryRepository

    @Test
    fun `test Repository`() {
        val id = NEW_ID

        // test size
        runBlocking {
            launch(Dispatchers.IO) {
                val size = dictionaryRepository.findAll().count()
                Assertions.assertEquals(4, size)
            }
        }

        // test uniq field
        val product = DictionaryDb(id, "Источник дохода")
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    dictionaryRepository.create(product)
                } catch (e: Exception) {
                    log.debug("This is normal")
                }
            }
            val optionalProduct = dictionaryRepository.findById(id)
            Assertions.assertTrue(optionalProduct == null)
        }

        // test create and update
        val dictionaryDb = DictionaryDb(id, NEW_NAME)
        runBlocking {
            dictionaryRepository.create(dictionaryDb)
            val newDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(newDictionary != null)
            val updateRecord = DictionaryDb(id, UPDATE_NAME)
            dictionaryRepository.update(updateRecord)
            val findDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(findDictionary?.name ==UPDATE_NAME)
        }

        // test delete by id
        val dictionaryDb2 = DictionaryDb(id, NEW_NAME)
        runBlocking {
            dictionaryRepository.deleteById(id)
            val findDictionary = dictionaryRepository.findById(id)
            Assertions.assertTrue(findDictionary == null)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DictionaryRepositoryTest::class.java)
        const val NEW_NAME = "Test value"
        const val UPDATE_NAME = "Changed value"
        const val NEW_ID = 7L
    }
}

