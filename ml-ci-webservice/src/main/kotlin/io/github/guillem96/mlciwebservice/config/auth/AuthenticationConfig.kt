package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.UserRepository
import io.github.guillem96.mlciwebservice.domain.User
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

        val mlModuleUsername = environment.getRequiredProperty("ml-module.user")
        val observerModuleUsername = environment.getRequiredProperty("observer-module.user")

        if (!usersRepository.existsByUsername(mlModuleUsername)) {
            usersRepository.save(
                    User(username = mlModuleUsername,
                            password = environment.getRequiredProperty("ml-module.pass"),
                            roles = listOf("ROLE_USER", "ROLE_MODULE")))

        }

        if (!usersRepository.existsByUsername(observerModuleUsername)) {
            usersRepository.save(
                    User(username = observerModuleUsername,
                            password = environment.getRequiredProperty("observer-module.pass"),
                            roles = listOf("ROLE_USER", "ROLE_MODULE")))
        }
    }
}