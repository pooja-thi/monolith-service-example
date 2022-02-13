package com.holobuilder.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Floor.
 */

@Entity
@Table(name = "floor")
data class Floor(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @get: NotNull

    @Column(name = "level", nullable = false)
    var level: Int? = null,

    @OneToMany(mappedBy = "floor")
    @JsonIgnoreProperties(
        value = [
            "floor",
        ],
        allowSetters = true
    )
    var rooms: MutableSet<Room>? = mutableSetOf(),

    @ManyToOne
    @JsonIgnoreProperties(
        value = [
            "floors",
        ],
        allowSetters = true
    )
    var project: Project? = null,
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addRoom(room: Room): Floor {
        this.rooms?.add(room)
        room.floor = this
        return this
    }
    fun removeRoom(room: Room): Floor {
        this.rooms?.remove(room)
        room.floor = null
        return this
    }
    fun project(project: Project?): Floor {
        this.project = project
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Floor) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "Floor{" +
            "id=" + id +
            ", level=" + level +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
