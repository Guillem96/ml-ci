package io.github.guillem96.mlciwebservice.domain

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

enum class LogLevel {
    INFO,
    DEBUG,
    WARNING,
    ERROR
}

@Entity
data class MlCiLog(
        @Id @GeneratedValue
        val id: Long? = null,

        @Enumerated(EnumType.STRING)
        val level: LogLevel,

        @NotBlank
        val message: String,

        @NotNull
        val buidNum: Int,

        @ManyToOne
        @NotNull
        val trackedRepository: TrackedRepository
)