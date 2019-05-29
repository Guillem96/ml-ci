package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
data class Approach(
        @NotBlank
        val name: String,                                               // Name of trained algorithm

        @ManyToOne
        @NotNull
        val trackedRepository: TrackedRepository,                       // Repository containing the current model metadata

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var buildNum: Int = 0,                                          // Trained nth times

        @Enumerated(EnumType.STRING)
        var status: ApproachStatus = ApproachStatus.NONE,               // Training status

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val trainDate: LocalDateTime = LocalDateTime.now(),             // Last train date

        @Id
        @GeneratedValue
        val id: Long? = null)


enum class ApproachStatus {
        TRAINING,
        TRAINED,
        ERROR,
        PENDENT,
        NONE
}