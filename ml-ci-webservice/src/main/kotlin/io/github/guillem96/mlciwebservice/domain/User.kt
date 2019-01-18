package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*
import javax.validation.constraints.NotBlank

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

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "user", fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val trackedRepositories: List<TrackedRepository> = emptyList(),

        @JsonIgnore
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: List<String> = listOf("USER"),

        var githubToken: String = "",

        @Id @GeneratedValue
        val id: Long? = null)
{

    fun encodePassword() { password = passwordEncoder.encode(password) }

    companion object {
        val passwordEncoder = BCryptPasswordEncoder()
    }
}