package com.holobuilder.web.rest

import com.holobuilder.IntegrationTest
import com.holobuilder.domain.Project
import com.holobuilder.repository.ProjectRepository
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
 * Integration tests for the [ProjectResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectResourceIT {
    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restProjectMockMvc: MockMvc

    private lateinit var project: Project

    @BeforeEach
    fun initTest() {
        project = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProject() {
        val databaseSizeBeforeCreate = projectRepository.findAll().size

        // Create the Project
        restProjectMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        ).andExpect(status().isCreated)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeCreate + 1)
        val testProject = projectList[projectList.size - 1]

        assertThat(testProject.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProjectWithExistingId() {
        // Create the Project with an existing ID
        project.id = 1L

        val databaseSizeBeforeCreate = projectRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        ).andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = projectRepository.findAll().size
        // set the field null
        project.name = null

        // Create the Project, which fails.

        restProjectMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        ).andExpect(status().isBadRequest)

        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProjects() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        // Get all the projectList
        restProjectMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(project.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val id = project.id
        assertNotNull(id)

        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, project.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(project.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProject() {
        // Get the project
        restProjectMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeUpdate = projectRepository.findAll().size

        // Update the project
        val updatedProject = projectRepository.findById(project.id).get()
        // Disconnect from session so that the updates on updatedProject are not directly saved in db
        em.detach(updatedProject)
        updatedProject.name = UPDATED_NAME

        restProjectMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedProject.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedProject))
        ).andExpect(status().isOk)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
        val testProject = projectList[projectList.size - 1]
        assertThat(testProject.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun putNonExistingProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            put(ENTITY_API_URL_ID, project.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        )
            .andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        ).andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(project))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateProjectWithPatch() {
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeUpdate = projectRepository.findAll().size

// Update the project using partial update
        val partialUpdatedProject = Project().apply {
            id = project.id

            name = UPDATED_NAME
        }

        restProjectMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProject.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProject))
        )
            .andExpect(status().isOk)

// Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
        val testProject = projectList.last()
        assertThat(testProject.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateProjectWithPatch() {
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeUpdate = projectRepository.findAll().size

// Update the project using partial update
        val partialUpdatedProject = Project().apply {
            id = project.id

            name = UPDATED_NAME
        }

        restProjectMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProject.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProject))
        )
            .andExpect(status().isOk)

// Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
        val testProject = projectList.last()
        assertThat(testProject.name).isEqualTo(UPDATED_NAME)
    }

    @Throws(Exception::class)
    fun patchNonExistingProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            patch(ENTITY_API_URL_ID, project.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(project))
        )
            .andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(project))
        )
            .andExpect(status().isBadRequest)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamProject() {
        val databaseSizeBeforeUpdate = projectRepository.findAll().size
        project.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(project))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Project in the database
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProject() {
        // Initialize the database
        projectRepository.saveAndFlush(project)

        val databaseSizeBeforeDelete = projectRepository.findAll().size

        // Delete the project
        restProjectMockMvc.perform(
            delete(ENTITY_API_URL_ID, project.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val projectList = projectRepository.findAll()
        assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/projects"
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
        fun createEntity(em: EntityManager): Project {
            val project = Project(

                name = DEFAULT_NAME

            )

            return project
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Project {
            val project = Project(

                name = UPDATED_NAME

            )

            return project
        }
    }
}
