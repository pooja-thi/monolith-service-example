package com.holobuilder.repository

import com.holobuilder.domain.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Room] entity.
 */
@Suppress("unused")
@Repository
interface RoomRepository : JpaRepository<Room, Long>
