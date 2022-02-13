package com.holobuilder.service.impl

import com.holobuilder.domain.Room
import com.holobuilder.repository.RoomRepository
import com.holobuilder.service.RoomService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Room].
 */
@Service
@Transactional
class RoomServiceImpl(
    private val roomRepository: RoomRepository
) : RoomService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(room: Room): Room {
        log.debug("Request to save Room : $room")
        return roomRepository.save(room)
    }

    override fun partialUpdate(room: Room): Optional<Room> {
        log.debug("Request to partially update Room : {}", room)

        return roomRepository.findById(room.id)
            .map {

                if (room.mediaURL != null) {
                    it.mediaURL = room.mediaURL
                }

                it
            }
            .map { roomRepository.save(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Room> {
        log.debug("Request to get all Rooms")
        return roomRepository.findAll()
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Room> {
        log.debug("Request to get Room : $id")
        return roomRepository.findById(id)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Room : $id")

        roomRepository.deleteById(id)
    }
}
