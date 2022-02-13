package com.holobuilder.web.rest

import com.holobuilder.IntegrationTest
import com.holobuilder.domain.Room
import com.holobuilder.repository.RoomRepository
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
 * Integration tests for the [RoomResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoomResourceIT {
    @Autowired
    private lateinit var roomRepository: RoomRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restRoomMockMvc: MockMvc

    private lateinit var room: Room

    @BeforeEach
    fun initTest() {
        room = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRoom() {
        val databaseSizeBeforeCreate = roomRepository.findAll().size

        // Create the Room
        restRoomMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(room))
        ).andExpect(status().isCreated)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeCreate + 1)
        val testRoom = roomList[roomList.size - 1]

        assertThat(testRoom.mediaURL).isEqualTo(DEFAULT_MEDIA_URL)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRoomWithExistingId() {
        // Create the Room with an existing ID
        room.id = 1L

        val databaseSizeBeforeCreate = roomRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(room))
        ).andExpect(status().isBadRequest)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllRooms() {
        // Initialize the database
        roomRepository.saveAndFlush(room)

        // Get all the roomList
        restRoomMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(room.id?.toInt())))
            .andExpect(jsonPath("$.[*].mediaURL").value(hasItem(DEFAULT_MEDIA_URL)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getRoom() {
        // Initialize the database
        roomRepository.saveAndFlush(room)

        val id = room.id
        assertNotNull(id)

        // Get the room
        restRoomMockMvc.perform(get(ENTITY_API_URL_ID, room.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(room.id?.toInt()))
            .andExpect(jsonPath("$.mediaURL").value(DEFAULT_MEDIA_URL))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingRoom() {
        // Get the room
        restRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewRoom() {
        // Initialize the database
        roomRepository.saveAndFlush(room)

        val databaseSizeBeforeUpdate = roomRepository.findAll().size

        // Update the room
        val updatedRoom = roomRepository.findById(room.id).get()
        // Disconnect from session so that the updates on updatedRoom are not directly saved in db
        em.detach(updatedRoom)
        updatedRoom.mediaURL = UPDATED_MEDIA_URL

        restRoomMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedRoom.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedRoom))
        ).andExpect(status().isOk)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
        val testRoom = roomList[roomList.size - 1]
        assertThat(testRoom.mediaURL).isEqualTo(UPDATED_MEDIA_URL)
    }

    @Test
    @Transactional
    fun putNonExistingRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            put(ENTITY_API_URL_ID, room.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(room))
        )
            .andExpect(status().isBadRequest)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(room))
        ).andExpect(status().isBadRequest)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(room))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateRoomWithPatch() {
        roomRepository.saveAndFlush(room)

        val databaseSizeBeforeUpdate = roomRepository.findAll().size

// Update the room using partial update
        val partialUpdatedRoom = Room().apply {
            id = room.id

            mediaURL = UPDATED_MEDIA_URL
        }

        restRoomMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRoom.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRoom))
        )
            .andExpect(status().isOk)

// Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
        val testRoom = roomList.last()
        assertThat(testRoom.mediaURL).isEqualTo(UPDATED_MEDIA_URL)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateRoomWithPatch() {
        roomRepository.saveAndFlush(room)

        val databaseSizeBeforeUpdate = roomRepository.findAll().size

// Update the room using partial update
        val partialUpdatedRoom = Room().apply {
            id = room.id

            mediaURL = UPDATED_MEDIA_URL
        }

        restRoomMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRoom.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRoom))
        )
            .andExpect(status().isOk)

// Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
        val testRoom = roomList.last()
        assertThat(testRoom.mediaURL).isEqualTo(UPDATED_MEDIA_URL)
    }

    @Throws(Exception::class)
    fun patchNonExistingRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            patch(ENTITY_API_URL_ID, room.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(room))
        )
            .andExpect(status().isBadRequest)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(room))
        )
            .andExpect(status().isBadRequest)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamRoom() {
        val databaseSizeBeforeUpdate = roomRepository.findAll().size
        room.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(room))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Room in the database
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteRoom() {
        // Initialize the database
        roomRepository.saveAndFlush(room)

        val databaseSizeBeforeDelete = roomRepository.findAll().size

        // Delete the room
        restRoomMockMvc.perform(
            delete(ENTITY_API_URL_ID, room.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val roomList = roomRepository.findAll()
        assertThat(roomList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_MEDIA_URL = "AAAAAAAAAA"
        private const val UPDATED_MEDIA_URL = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/rooms"
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
        fun createEntity(em: EntityManager): Room {
            val room = Room(

                mediaURL = DEFAULT_MEDIA_URL

            )

            return room
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Room {
            val room = Room(

                mediaURL = UPDATED_MEDIA_URL

            )

            return room
        }
    }
}
