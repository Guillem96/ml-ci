package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component


@Component
class BasicUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("Incorrect username")
        return with(user) {
            User.withUsername(user.username)
                    .password(user.password)
                    .authorities(user.roles.map { SimpleGrantedAuthority(it) })
                    .build()
        }
    }
}