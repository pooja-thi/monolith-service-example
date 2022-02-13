package com.holobuilder.web.rest

import com.holobuilder.domain.Room
import com.holobuilder.repository.RoomRepository
import com.holobuilder.service.RoomService
import com.holobuilder.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects

private const val ENTITY_NAME = "room"
/**
 * REST controller for managing [com.holobuilder.domain.Room].
 */
@RestController
@RequestMapping("/api")
class RoomResource(
    private val roomService: RoomService,
    private val roomRepository: RoomRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "room"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /rooms` : Create a new room.
     *
     * @param room the room to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new room, or with status `400 (Bad Request)` if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rooms")
    fun createRoom(@RequestBody room: Room): ResponseEntity<Room> {
        log.debug("REST request to save Room : $room")
        if (room.id != null) {
            throw BadRequestAlertException(
                "A new room cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = roomService.save(room)
        return ResponseEntity.created(URI("/api/rooms/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /rooms/:id} : Updates an existing room.
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated room,
     * or with status `400 (Bad Request)` if the room is not valid,
     * or with status `500 (Internal Server Error)` if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rooms/{id}")
    fun updateRoom(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody room: Room
    ): ResponseEntity<Room> {
        log.debug("REST request to update Room : {}, {}", id, room)
        if (room.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, room.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!roomRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = roomService.save(room)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    room.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /rooms/:id} : Partial updates given fields of an existing room, field will ignore if it is null
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated room,
     * or with status {@code 400 (Bad Request)} if the room is not valid,
     * or with status {@code 404 (Not Found)} if the room is not found,
     * or with status {@code 500 (Internal Server Error)} if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/rooms/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateRoom(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody room: Room
    ): ResponseEntity<Room> {
        log.debug("REST request to partial update Room partially : {}, {}", id, room)
        if (room.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, room.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!roomRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = roomService.partialUpdate(room)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, room.id.toString())
        )
    }

    /**
     * `GET  /rooms` : get all the rooms.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of rooms in body.
     */
    @GetMapping("/rooms")
    fun getAllRooms(): MutableList<Room> {
        log.debug("REST request to get all Rooms")

        return roomService.findAll()
    }

    /**
     * `GET  /rooms/:id` : get the "id" room.
     *
     * @param id the id of the room to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the room, or with status `404 (Not Found)`.
     */
    @GetMapping("/rooms/{id}")
    fun getRoom(@PathVariable id: Long): ResponseEntity<Room> {
        log.debug("REST request to get Room : $id")
        val room = roomService.findOne(id)
        return ResponseUtil.wrapOrNotFound(room)
    }
    /**
     *  `DELETE  /rooms/:id` : delete the "id" room.
     *
     * @param id the id of the room to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/rooms/{id}")
    fun deleteRoom(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Room : $id")

        roomService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
