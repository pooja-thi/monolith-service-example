package com.holobuilder.repository

import com.holobuilder.domain.Floor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Floor] entity.
 */
@Suppress("unused")
@Repository
interface FloorRepository : JpaRepository<Floor, Long>
