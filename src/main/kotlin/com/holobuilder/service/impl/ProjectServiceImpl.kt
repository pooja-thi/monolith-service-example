package com.holobuilder.service.impl

import com.holobuilder.domain.Project
import com.holobuilder.repository.ProjectRepository
import com.holobuilder.service.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Project].
 */
@Service
@Transactional
class ProjectServiceImpl(
    private val projectRepository: ProjectRepository
) : ProjectService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(project: Project): Project {
        log.debug("Request to save Project : $project")
        return projectRepository.save(project)
    }

    override fun partialUpdate(project: Project): Optional<Project> {
        log.debug("Request to partially update Project : {}", project)

        return projectRepository.findById(project.id)
            .map {

                if (project.name != null) {
                    it.name = project.name
                }

                it
            }
            .map { projectRepository.save(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Project> {
        log.debug("Request to get all Projects")
        return projectRepository.findAll()
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Project> {
        log.debug("Request to get Project : $id")
        return projectRepository.findById(id)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Project : $id")

        projectRepository.deleteById(id)
    }
}
