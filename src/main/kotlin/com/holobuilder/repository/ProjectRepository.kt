package com.holobuilder.repository

import com.holobuilder.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Project] entity.
 */
@Suppress("unused")
@Repository
interface ProjectRepository : JpaRepository<Project, Long>
