package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.User
import io.github.guillem96.mlciwebservice.UserRepository
import io.github.guillem96.mlciwebservice.config.auth.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
        private val authenticationManager: AuthenticationManager,
        private val jwtTokenProvider: JwtTokenProvider,
        private val userRepository: UserRepository
) {
    @PostMapping("/signIn")
    fun signIn(@RequestBody data: Credentials): ResponseEntity<AuthResponse> {

        if (data.username.isEmpty() or data.password.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(data.username, data.password))

            val user = userRepository.findByUsername(data.username) ?: return ResponseEntity.notFound().build()
            val token: String = jwtTokenProvider.createToken(data.username, user.roles)
            return ok(AuthResponse(user, token))
        }catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
}

// Representing json structures
data class Credentials(val username: String, val password: String)
data class AuthResponse(val user: User, val token: String)
