package io.github.guillem96.mlciwebservice

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Record(
        @NotBlank
        val title: String,

        @Id @GeneratedValue val
        id: Long? = null)


@Entity
@Table(name = "users")
data class User(
        @Column(unique = true)
        @NotBlank
        val username: String,

        @get:JsonIgnore
        @NotBlank
        val password: String,

        val email: String,

        @JsonIgnore
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: List<String> = listOf("USER"),

        @OneToOne
        val githubCredentials: GitHubCredentials? = null,

        @Id @GeneratedValue
        val id: Long? = null)
{
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