package com.holobuilder.domain

import com.holobuilder.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FloorTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Floor::class)
        val floor1 = Floor()
        floor1.id = 1L
        val floor2 = Floor()
        floor2.id = floor1.id
        assertThat(floor1).isEqualTo(floor2)
        floor2.id = 2L
        assertThat(floor1).isNotEqualTo(floor2)
        floor1.id = null
        assertThat(floor1).isNotEqualTo(floor2)
    }
}
