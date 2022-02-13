package com.holobuilder.web.rest

import com.holobuilder.domain.Project
import com.holobuilder.repository.ProjectRepository
import com.holobuilder.service.ProjectService
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

private const val ENTITY_NAME = "project"
/**
 * REST controller for managing [com.holobuilder.domain.Project].
 */
@RestController
@RequestMapping("/api")
class ProjectResource(
    private val projectService: ProjectService,
    private val projectRepository: ProjectRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "project"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /projects` : Create a new project.
     *
     * @param project the project to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new project, or with status `400 (Bad Request)` if the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projects")
    fun createProject(@Valid @RequestBody project: Project): ResponseEntity<Project> {
        log.debug("REST request to save Project : $project")
        if (project.id != null) {
            throw BadRequestAlertException(
                "A new project cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = projectService.save(project)
        return ResponseEntity.created(URI("/api/projects/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /projects/:id} : Updates an existing project.
     *
     * @param id the id of the project to save.
     * @param project the project to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated project,
     * or with status `400 (Bad Request)` if the project is not valid,
     * or with status `500 (Internal Server Error)` if the project couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projects/{id}")
    fun updateProject(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody project: Project
    ): ResponseEntity<Project> {
        log.debug("REST request to update Project : {}, {}", id, project)
        if (project.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, project.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!projectRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = projectService.save(project)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    project.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /projects/:id} : Partial updates given fields of an existing project, field will ignore if it is null
     *
     * @param id the id of the project to save.
     * @param project the project to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated project,
     * or with status {@code 400 (Bad Request)} if the project is not valid,
     * or with status {@code 404 (Not Found)} if the project is not found,
     * or with status {@code 500 (Internal Server Error)} if the project couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/projects/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateProject(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody project: Project
    ): ResponseEntity<Project> {
        log.debug("REST request to partial update Project partially : {}, {}", id, project)
        if (project.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, project.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!projectRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = projectService.partialUpdate(project)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, project.id.toString())
        )
    }

    /**
     * `GET  /projects` : get all the projects.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of projects in body.
     */
    @GetMapping("/projects")
    fun getAllProjects(): MutableList<Project> {
        log.debug("REST request to get all Projects")

        return projectService.findAll()
    }

    /**
     * `GET  /projects/:id` : get the "id" project.
     *
     * @param id the id of the project to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the project, or with status `404 (Not Found)`.
     */
    @GetMapping("/projects/{id}")
    fun getProject(@PathVariable id: Long): ResponseEntity<Project> {
        log.debug("REST request to get Project : $id")
        val project = projectService.findOne(id)
        return ResponseUtil.wrapOrNotFound(project)
    }
    /**
     *  `DELETE  /projects/:id` : delete the "id" project.
     *
     * @param id the id of the project to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/projects/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Project : $id")

        projectService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
