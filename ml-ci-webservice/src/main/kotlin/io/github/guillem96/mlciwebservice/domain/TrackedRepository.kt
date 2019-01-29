package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class TrackedRepository(
        @NotBlank
        val url: String,                                            // Access URI to repository

        val lastCommit: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val user: User? = null,                                     // User owning the repository

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "trackedRepository")
        val models: List<Model> = emptyList(),                      // All trained models related to repository

        @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var buildNum: Int = 0,                                      // Trained nth times

        @Id @GeneratedValue
        val id: Long? = null) {

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val lastTrain: List<Model>
        get() = models.filter { it.buildNum == buildNum }           // Models of current `buildNum`

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @get:Enumerated(EnumType.STRING)
    val status: ModelStatus                                         // Repository status based on its models
        get() {
            val modelStatuses =  lastTrain.map { it.status }
            return when {
                modelStatuses.isEmpty() -> ModelStatus.NONE
                modelStatuses.contains(ModelStatus.ERROR) -> ModelStatus.ERROR
                modelStatuses.contains(ModelStatus.TRAINING) -> ModelStatus.TRAINING
                modelStatuses.all { it == ModelStatus.TRAINED } -> ModelStatus.TRAINED
                modelStatuses.all { it == ModelStatus.PENDENT} -> ModelStatus.PENDENT
                else -> ModelStatus.NONE
            }
        }

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val trainDate: LocalDateTime?                                   // Largest train date from trained models
        get() = lastTrain.map { it.trainDate }.max()
}