package com.holobuilder.service
import com.holobuilder.domain.Project
import java.util.Optional

/**
 * Service Interface for managing [Project].
 */
interface ProjectService {

    /**
     * Save a project.
     *
     * @param project the entity to save.
     * @return the persisted entity.
     */
    fun save(project: Project): Project

    /**
     * Partially updates a project.
     *
     * @param project the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(project: Project): Optional<Project>

    /**
     * Get all the projects.
     *
     * @return the list of entities.
     */
    fun findAll(): MutableList<Project>

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<Project>

    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
