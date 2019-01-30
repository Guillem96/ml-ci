package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class TrackedRepository(
        @NotBlank
        val url: String,                                            // Access URI to repository

        val lastCommit: String,                                     // Last github commit. Control changes

        var aborted: Boolean = false,                               // True means that the training cannot be performed

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "trackedRepository")
        val logs: List<Log> = emptyList(),                          // Logs during last training

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
    val lastLogs: List<Log>
        get() = logs.filter { it.buildNum == buildNum }

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @get:Enumerated(EnumType.STRING)
    val status: ModelStatus                                         // Repository status based on its models
        get() {
            if (aborted)
                return ModelStatus.ERROR

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


@Entity
data class Log(
        @Enumerated(EnumType.STRING)
        val logLevel: LogLevel,                             // Log importance ERROR, INFO, SUCCESS

        @NotBlank
        val message: String,                                // Log message

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val dateTime: LocalDateTime = LocalDateTime.now(),  // Date time when the log has been created

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val buildNum: Int,

        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val trackedRepository: TrackedRepository,

        @Id @GeneratedValue
        val id: Long? = null)

enum class LogLevel {
    INFO,
    SUCCESS,
    ERROR,
}