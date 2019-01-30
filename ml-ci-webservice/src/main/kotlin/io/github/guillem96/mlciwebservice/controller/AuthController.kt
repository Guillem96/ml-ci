package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.domain.User
import io.github.guillem96.mlciwebservice.UserRepository
import io.github.guillem96.mlciwebservice.config.auth.JwtTokenProvider
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RepositoryRestController
@RequestMapping("/auth")
class AuthController(
        private val authenticationManager: AuthenticationManager,
        private val jwtTokenProvider: JwtTokenProvider,
        private val userRepository: UserRepository
) {

    @PostMapping("/signIn")
    fun signIn(@RequestBody data: Credentials,
               resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<AuthResponse> {

        if (data.username.isEmpty() or data.password.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(data.username, data.password))

            // Find user or return 404
            val user = userRepository.findByUsername(data.username) ?: return ResponseEntity.notFound().build()

            // Update github access token every sign in
            user.githubToken = data.githubToken
            userRepository.save(user)

            // Generate and return token
            val token: String = jwtTokenProvider.createToken(data.username, user.roles)
            return ok(AuthResponse(user, token))
        }catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}

// Sign in request body
data class Credentials(val username: String, val password: String, val githubToken: String = "")

// Sign in response body
data class AuthResponse(val user: User, val token: String)