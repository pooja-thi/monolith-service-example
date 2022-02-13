package com.holobuilder.domain

import com.holobuilder.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoomTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Room::class)
        val room1 = Room()
        room1.id = 1L
        val room2 = Room()
        room2.id = room1.id
        assertThat(room1).isEqualTo(room2)
        room2.id = 2L
        assertThat(room1).isNotEqualTo(room2)
        room1.id = null
        assertThat(room1).isNotEqualTo(room2)
    }
}
