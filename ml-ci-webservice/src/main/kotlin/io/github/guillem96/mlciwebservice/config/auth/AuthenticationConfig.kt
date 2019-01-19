package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.domain.User
import io.github.guillem96.mlciwebservice.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter

@Configuration
class AuthenticationConfig(
        private val environment: Environment,
        private val basicUserDetailsService: BasicUserDetailsService,
        private val usersRepository: UserRepository
): GlobalAuthenticationConfigurerAdapter() {

    @Throws(Exception::class)
    override fun init(auth: AuthenticationManagerBuilder)  {
        auth
                .userDetailsService(basicUserDetailsService)
                .passwordEncoder(User.passwordEncoder)

        usersRepository.save(
                User(username = environment.getRequiredProperty("ml-module.user"),
                        password = User.passwordEncoder.encode(environment.getRequiredProperty("ml-module.pass")),
                        roles = listOf("USER", "MODULE")))
    }
}