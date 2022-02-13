package com.holobuilder.web.rest

import com.holobuilder.IntegrationTest
import com.holobuilder.domain.Floor
import com.holobuilder.repository.FloorRepository
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [FloorResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FloorResourceIT {
    @Autowired
    private lateinit var floorRepository: FloorRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restFloorMockMvc: MockMvc

    private lateinit var floor: Floor

    @BeforeEach
    fun initTest() {
        floor = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createFloor() {
        val databaseSizeBeforeCreate = floorRepository.findAll().size

        // Create the Floor
        restFloorMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        ).andExpect(status().isCreated)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeCreate + 1)
        val testFloor = floorList[floorList.size - 1]

        assertThat(testFloor.level).isEqualTo(DEFAULT_LEVEL)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createFloorWithExistingId() {
        // Create the Floor with an existing ID
        floor.id = 1L

        val databaseSizeBeforeCreate = floorRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restFloorMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        ).andExpect(status().isBadRequest)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkLevelIsRequired() {
        val databaseSizeBeforeTest = floorRepository.findAll().size
        // set the field null
        floor.level = null

        // Create the Floor, which fails.

        restFloorMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        ).andExpect(status().isBadRequest)

        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllFloors() {
        // Initialize the database
        floorRepository.saveAndFlush(floor)

        // Get all the floorList
        restFloorMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(floor.id?.toInt())))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getFloor() {
        // Initialize the database
        floorRepository.saveAndFlush(floor)

        val id = floor.id
        assertNotNull(id)

        // Get the floor
        restFloorMockMvc.perform(get(ENTITY_API_URL_ID, floor.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(floor.id?.toInt()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingFloor() {
        // Get the floor
        restFloorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewFloor() {
        // Initialize the database
        floorRepository.saveAndFlush(floor)

        val databaseSizeBeforeUpdate = floorRepository.findAll().size

        // Update the floor
        val updatedFloor = floorRepository.findById(floor.id).get()
        // Disconnect from session so that the updates on updatedFloor are not directly saved in db
        em.detach(updatedFloor)
        updatedFloor.level = UPDATED_LEVEL

        restFloorMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedFloor.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedFloor))
        ).andExpect(status().isOk)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
        val testFloor = floorList[floorList.size - 1]
        assertThat(testFloor.level).isEqualTo(UPDATED_LEVEL)
    }

    @Test
    @Transactional
    fun putNonExistingFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            put(ENTITY_API_URL_ID, floor.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        )
            .andExpect(status().isBadRequest)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        ).andExpect(status().isBadRequest)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(floor))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateFloorWithPatch() {
        floorRepository.saveAndFlush(floor)

        val databaseSizeBeforeUpdate = floorRepository.findAll().size

// Update the floor using partial update
        val partialUpdatedFloor = Floor().apply {
            id = floor.id
        }

        restFloorMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedFloor.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedFloor))
        )
            .andExpect(status().isOk)

// Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
        val testFloor = floorList.last()
        assertThat(testFloor.level).isEqualTo(DEFAULT_LEVEL)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateFloorWithPatch() {
        floorRepository.saveAndFlush(floor)

        val databaseSizeBeforeUpdate = floorRepository.findAll().size

// Update the floor using partial update
        val partialUpdatedFloor = Floor().apply {
            id = floor.id

            level = UPDATED_LEVEL
        }

        restFloorMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedFloor.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedFloor))
        )
            .andExpect(status().isOk)

// Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
        val testFloor = floorList.last()
        assertThat(testFloor.level).isEqualTo(UPDATED_LEVEL)
    }

    @Throws(Exception::class)
    fun patchNonExistingFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            patch(ENTITY_API_URL_ID, floor.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(floor))
        )
            .andExpect(status().isBadRequest)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(floor))
        )
            .andExpect(status().isBadRequest)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamFloor() {
        val databaseSizeBeforeUpdate = floorRepository.findAll().size
        floor.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFloorMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(floor))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Floor in the database
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteFloor() {
        // Initialize the database
        floorRepository.saveAndFlush(floor)

        val databaseSizeBeforeDelete = floorRepository.findAll().size

        // Delete the floor
        restFloorMockMvc.perform(
            delete(ENTITY_API_URL_ID, floor.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val floorList = floorRepository.findAll()
        assertThat(floorList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_LEVEL: Int = 1
        private const val UPDATED_LEVEL: Int = 2

        private val ENTITY_API_URL: String = "/api/floors"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Floor {
            val floor = Floor(

                level = DEFAULT_LEVEL

            )

            return floor
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Floor {
            val floor = Floor(

                level = UPDATED_LEVEL

            )

            return floor
        }
    }
}
