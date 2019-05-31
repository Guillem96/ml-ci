package io.github.guillem96.mlciwebservice.domain

import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import org.springframework.data.domain.Persistable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "users")
data class User(
        @Column(unique = true)
        @NotBlank
        val username: String,                                           // Username

        //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank @Length(min=8, max=256)
        var password: String = "",                                      // Encrypted user's password

        val email: String = "",                                         // User's mail

        @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "user", fetch = FetchType.LAZY)
        @JsonIdentityReference(alwaysAsId = true)
        val trackedRepositories: List<TrackedRepository> = emptyList(), // All repositories owned by user

        @JsonIgnore
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: List<String> = listOf("ROLE_USER"),                  // User roles

        var githubToken: String = "",                                   // Github credentials

        @Id @GeneratedValue
        val id: Long? = null)
{

    fun encodePassword() { password = passwordEncoder.encode(password) }

    // Global password encoder
    companion object {
        val passwordEncoder = BCryptPasswordEncoder()
    }
}