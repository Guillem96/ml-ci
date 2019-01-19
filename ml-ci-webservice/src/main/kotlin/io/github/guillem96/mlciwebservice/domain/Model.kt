package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.*
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
data class Model(
        @NotBlank
        val algorithm: String,

        @ElementCollection(targetClass=Pair::class)
        val hyperParameters: Map<String, Any> = emptyMap(),

        @ManyToOne
        @NotNull
        val trackedRepository: TrackedRepository,

        @Enumerated(EnumType.STRING)
        var status: ModelStatus = ModelStatus.NONE,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val trainDate: LocalDateTime = LocalDateTime.now(),

        @ElementCollection(targetClass=Pair::class)
        val evaluations: Map<String, Double> = emptyMap(),

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