package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class TrackedRepository(
        @NotBlank
        val url: String,

        val lastCommit: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val user: User? = null,

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "trackedRepository")
        val models: List<Model> = emptyList(),

        @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var buildNum: Int = 0,

        @Id @GeneratedValue
        val id: Long? = null) {

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @get:Enumerated(EnumType.STRING)
    val status: ModelStatus
        get() {
            val modelStatuses =  models.map { it.status }
            return when {
                modelStatuses.contains(ModelStatus.ERROR) -> ModelStatus.ERROR
                modelStatuses.contains(ModelStatus.TRAINING) -> ModelStatus.TRAINING
                modelStatuses.all { it == ModelStatus.TRAINED } -> ModelStatus.TRAINED
                modelStatuses.all { it == ModelStatus.PENDENT} -> ModelStatus.PENDENT
                else -> ModelStatus.NONE
            }
        }

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val trainDate: LocalDateTime?
        get() = models.map { it.trainDate }.max()
}