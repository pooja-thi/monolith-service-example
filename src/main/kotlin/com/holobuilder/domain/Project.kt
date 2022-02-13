package com.holobuilder.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Project.
 */

@Entity
@Table(name = "project")
data class Project(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @get: NotNull

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @OneToMany(mappedBy = "project")
    @JsonIgnoreProperties(
        value = [
            "rooms",
            "project",
        ],
        allowSetters = true
    )
    var floors: MutableSet<Floor>? = mutableSetOf(),
    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    fun addFloor(floor: Floor): Project {
        this.floors?.add(floor)
        floor.project = this
        return this
    }
    fun removeFloor(floor: Floor): Project {
        this.floors?.remove(floor)
        floor.project = null
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false
        return id != null && other.id != null && id == other.id
    }

    @Override
    override fun toString(): String {
        return "Project{" +
            "id=" + id +
            ", name='" + name + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
