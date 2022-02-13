package com.holobuilder.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*

/**
 * A Room.
 */

@Entity
@Table(name = "room")
data class Room(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "media_url")
    var mediaURL: String? = null,

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "rooms",
            "project",
        ],
        allowSetters = true
    )
    var floor: Floor? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun floor(floor: Floor?): Room {
        this.floor = floor
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Room) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "Room{" +
            "id=" + id +
            ", mediaURL='" + mediaURL + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
