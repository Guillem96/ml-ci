package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.*
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
data class Model(
        @NotBlank
        val algorithm: String,                                  // Name of trained algorithm

        @ElementCollection(targetClass=Pair::class)
        val hyperParameters: Map<String, Any> = emptyMap(),     // Algorithm training parameters

        @ManyToOne
        @NotNull
        val trackedRepository: TrackedRepository,               // Repository containing the current model metadata

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        var buildNum: Int = 0,                                  // Trained nth times

        @Enumerated(EnumType.STRING)
        var status: ModelStatus = ModelStatus.NONE,             // Training status

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val trainDate: LocalDateTime = LocalDateTime.now(),     // Last train date

        @ElementCollection(targetClass=Pair::class)
        val evaluations: MutableMap<String, Double> = HashMap(),// Results of training

        @Id
        @GeneratedValue
        val id: Long? = null)


enum class ModelStatus {
        TRAINING,
        TRAINED,
        ERROR,
        PENDENT,
        NONE
}