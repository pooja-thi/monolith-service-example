package com.holobuilder.service
import com.holobuilder.domain.Floor
import java.util.Optional

/**
 * Service Interface for managing [Floor].
 */
interface FloorService {

    /**
     * Save a floor.
     *
     * @param floor the entity to save.
     * @return the persisted entity.
     */
    fun save(floor: Floor): Floor

    /**
     * Partially updates a floor.
     *
     * @param floor the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(floor: Floor): Optional<Floor>

    /**
     * Get all the floors.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Floor>

    /**
     * Get the "id" floor.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Floor>

    /**
     * Delete the "id" floor.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
