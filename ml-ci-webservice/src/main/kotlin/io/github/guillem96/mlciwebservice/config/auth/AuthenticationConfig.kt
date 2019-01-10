package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.User
import io.github.guillem96.mlciwebservice.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter

@Configuration
class AuthenticationConfig(
        private val basicUserDetailsService: BasicUserDetailsService,
        private val usersRepository: UserRepository
): GlobalAuthenticationConfigurerAdapter() {

    @Throws(Exception::class)
    override fun init(auth: AuthenticationManagerBuilder)  {
        auth
                .userDetailsService(basicUserDetailsService)
                .passwordEncoder(User.passwordEncoder)

        if (!usersRepository.existsByUsername("test")) {
            val user = User(username = "test",
                    password = User.passwordEncoder.encode("password"),
                    email = "test@gmail.com")
            usersRepository.save(user)
        }
    }
}