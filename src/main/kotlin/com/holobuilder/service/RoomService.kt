package com.holobuilder.service
import com.holobuilder.domain.Room
import java.util.Optional

/**
 * Service Interface for managing [Room].
 */
interface RoomService {

    /**
     * Save a room.
     *
     * @param room the entity to save.
     * @return the persisted entity.
     */
    fun save(room: Room): Room

    /**
     * Partially updates a room.
     *
     * @param room the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(room: Room): Optional<Room>

    /**
     * Get all the rooms.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Room>

    /**
     * Get the "id" room.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Room>

    /**
     * Delete the "id" room.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
