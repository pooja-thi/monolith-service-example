package com.holobuilder.web.rest

import com.holobuilder.domain.Floor
import com.holobuilder.repository.FloorRepository
import com.holobuilder.service.FloorService
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
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "floor"
/**
 * REST controller for managing [com.holobuilder.domain.Floor].
 */
@RestController
@RequestMapping("/api")
class FloorResource(
    private val floorService: FloorService,
    private val floorRepository: FloorRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "floor"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /floors` : Create a new floor.
     *
     * @param floor the floor to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new floor, or with status `400 (Bad Request)` if the floor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/floors")
    fun createFloor(@Valid @RequestBody floor: Floor): ResponseEntity<Floor> {
        log.debug("REST request to save Floor : $floor")
        if (floor.id != null) {
            throw BadRequestAlertException(
                "A new floor cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = floorService.save(floor)
        return ResponseEntity.created(URI("/api/floors/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /floors/:id} : Updates an existing floor.
     *
     * @param id the id of the floor to save.
     * @param floor the floor to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated floor,
     * or with status `400 (Bad Request)` if the floor is not valid,
     * or with status `500 (Internal Server Error)` if the floor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/floors/{id}")
    fun updateFloor(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody floor: Floor
    ): ResponseEntity<Floor> {
        log.debug("REST request to update Floor : {}, {}", id, floor)
        if (floor.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, floor.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!floorRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = floorService.save(floor)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    floor.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /floors/:id} : Partial updates given fields of an existing floor, field will ignore if it is null
     *
     * @param id the id of the floor to save.
     * @param floor the floor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated floor,
     * or with status {@code 400 (Bad Request)} if the floor is not valid,
     * or with status {@code 404 (Not Found)} if the floor is not found,
     * or with status {@code 500 (Internal Server Error)} if the floor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/floors/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateFloor(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody floor: Floor
    ): ResponseEntity<Floor> {
        log.debug("REST request to partial update Floor partially : {}, {}", id, floor)
        if (floor.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, floor.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!floorRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = floorService.partialUpdate(floor)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, floor.id.toString())
        )
    }

    /**
     * `GET  /floors` : get all the floors.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of floors in body.
     */
    @GetMapping("/floors")
    fun getAllFloors(): MutableList<Floor> {
        log.debug("REST request to get all Floors")

        return floorService.findAll()
    }

    /**
     * `GET  /floors/:id` : get the "id" floor.
     *
     * @param id the id of the floor to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the floor, or with status `404 (Not Found)`.
     */
    @GetMapping("/floors/{id}")
    fun getFloor(@PathVariable id: Long): ResponseEntity<Floor> {
        log.debug("REST request to get Floor : $id")
        val floor = floorService.findOne(id)
        return ResponseUtil.wrapOrNotFound(floor)
    }
    /**
     *  `DELETE  /floors/:id` : delete the "id" floor.
     *
     * @param id the id of the floor to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/floors/{id}")
    fun deleteFloor(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Floor : $id")

        floorService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
