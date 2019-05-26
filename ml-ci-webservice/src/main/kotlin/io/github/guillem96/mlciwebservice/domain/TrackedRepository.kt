package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class TrackedRepository(
        @NotBlank
        val url: String,                                                // Access URI to repository

        val lastCommit: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val user: User? = null,                                         // User owning the repository

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "trackedRepository")
        val approaches: List<Approach> = emptyList(),                   // All trained models related to repository

        @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var buildNum: Int = 0,                                          // Trained nth times

        @Id @GeneratedValue
        val id: Long? = null) {

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val lastTrain: List<Approach>
        get() = approaches.filter { it.buildNum == buildNum }           // Models of current `buildNum`

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @get:Enumerated(EnumType.STRING)
    val status: ApproachStatus                                         // Repository status based on its models
        get() {
            val modelStatuses =  lastTrain.map { it.status }
            return when {
                modelStatuses.isEmpty() -> ApproachStatus.NONE
                modelStatuses.contains(ApproachStatus.ERROR) -> ApproachStatus.ERROR
                modelStatuses.contains(ApproachStatus.TRAINING) -> ApproachStatus.TRAINING
                modelStatuses.all { it == ApproachStatus.TRAINED } -> ApproachStatus.TRAINED
                modelStatuses.all { it == ApproachStatus.PENDENT} -> ApproachStatus.PENDENT
                else -> ApproachStatus.NONE
            }
        }

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val trainDate: LocalDateTime?                                   // Largest train date from trained models
        get() = lastTrain.map { it.trainDate }.max()
}