package com.pawga

import io.micronaut.core.annotation.NonNull
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.Sql
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.sql.Connection
import java.util.Map

@MicronautTest(startApplication = false) // <1>
@Testcontainers(disabledWithoutDocker = true) // <2>
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // <3>
@Sql(scripts = ["classpath:sql/seed-data-2.sql"], phase = Sql.Phase.BEFORE_EACH)
internal class DictionaryRepositoryWithoutUrlTest : TestPropertyProvider {
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
    var dictionaryRepository: DictionaryRepository? = null

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
        private const val NEW_NAME = "Test value"
        private const val UPDATE_NAME = "Changed value"

        @Container
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(
            "postgres:15.2-alpine"
        ).withCopyFileToContainer(
            MountableFile.forClasspathResource("sql/init-db-2.sql"),
            "/docker-entrypoint-initdb.d/init-db.sql"
        )
    }
}
