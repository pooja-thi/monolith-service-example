package com.holobuilder.service.impl

import com.holobuilder.domain.Floor
import com.holobuilder.repository.FloorRepository
import com.holobuilder.service.FloorService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Floor].
 */
@Service
@Transactional
class FloorServiceImpl(
    private val floorRepository: FloorRepository
) : FloorService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(floor: Floor): Floor {
        log.debug("Request to save Floor : $floor")
        return floorRepository.save(floor)
    }

    override fun partialUpdate(floor: Floor): Optional<Floor> {
        log.debug("Request to partially update Floor : {}", floor)

        return floorRepository.findById(floor.id)
            .map {

                if (floor.level != null) {
                    it.level = floor.level
                }

                it
            }
            .map { floorRepository.save(it) }
    }

    @Transactional(readOnly = true)
    override fun findAll(): MutableList<Floor> {
        log.debug("Request to get all Floors")
        return floorRepository.findAll()
    }

    @Transactional(readOnly = true)
    override fun findOne(id: Long): Optional<Floor> {
        log.debug("Request to get Floor : $id")
        return floorRepository.findById(id)
    }

    override fun delete(id: Long) {
        log.debug("Request to delete Floor : $id")

        floorRepository.deleteById(id)
    }
}
