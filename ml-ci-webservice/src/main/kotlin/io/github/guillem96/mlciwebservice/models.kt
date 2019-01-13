package io.github.guillem96.mlciwebservice

import com.fasterxml.jackson.annotation.*
import org.hibernate.validator.constraints.Length
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class TrackedRepository(
        @NotBlank
        val url: String,

        val lastCommit: String,

        @ManyToOne
        @NotNull
        val user: User,

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "trackedRepository")
        val models: List<Model> = emptyList(),


        @Id @GeneratedValue val
        id: Long? = null)


@Entity
@Table(name = "users")
data class User(
        @Column(unique = true)
        @NotBlank
        val username: String,

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank @Length(min=8, max=256)
        var password: String = "",

        val email: String = "",

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "user")
        val trackedRepositories: List<TrackedRepository> = emptyList(),

        @JsonIgnore
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: List<String> = listOf("USER"),

        @OneToOne
        val githubCredentials: GitHubCredentials? = null,

        @Id @GeneratedValue
        val id: Long? = null)
{

        fun encodePassword() { password = passwordEncoder.encode(password) }

        companion object {
            val passwordEncoder = BCryptPasswordEncoder()
        }
}

@Entity
data class GitHubCredentials(
        @OneToOne
        val user: User? = null,

        val token: String,

        @Id
        @GeneratedValue
        val id: Long? = null)

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
        val status: ModelStatus = ModelStatus.NONE,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        val trainDate: LocalDateTime = LocalDateTime.now(),

        @OneToOne(mappedBy = "model", cascade = [CascadeType.ALL])
        val evaluation: Evaluation? = null,

        @Id
        @GeneratedValue
        val id: Long? = null)


@Entity
data class Evaluation(
        @ElementCollection(targetClass=Pair::class)
        val results: Map<String, Double> = emptyMap(),

        @OneToOne
        @JoinColumn(name = "model_id")
        val model: Model,

        @Id
        @GeneratedValue
        val id: Long? = null
)

enum class ModelStatus {
        TRAINNING,
        TRAINED,
        PENDENT,
        NONE
}