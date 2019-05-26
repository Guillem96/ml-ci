package io.github.guillem96.mlciwebservice.config

import io.github.guillem96.mlciwebservice.ApproachRepository
import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.UserRepository
import io.github.guillem96.mlciwebservice.domain.Approach
import io.github.guillem96.mlciwebservice.domain.TrackedRepository
import io.github.guillem96.mlciwebservice.domain.User
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import javax.annotation.PostConstruct

@Configuration
class RepositoryRestConfig(private val environment: Environment,
                           private val userRepository: UserRepository,
                           private val trackedRepositoryRepository: TrackedRepositoryRepository,
                           private val approachRepository: ApproachRepository) : RepositoryRestConfigurer {

    @Override
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(TrackedRepository::class.java)
        config.exposeIdsFor(Approach::class.java)
        config.exposeIdsFor(User::class.java)
    }

    @PostConstruct
    fun init() {
        if(!environment.activeProfiles.contains("Test")) {
            if (!userRepository.existsByUsername("test")) {
                val user = User(username = "test",
                        password = User.passwordEncoder.encode("password"),
                        email = "test@gmail.com")
                userRepository.save(user)

                val trackedRepository = TrackedRepository(
                        url = "https://github.com/Guillem96/ml-ci-test-clf",
                        lastCommit = "5f792244a94136c418644ca60f7359475b7db831",
                        user = user)
                trackedRepositoryRepository.save(trackedRepository)
            }
        }
    }
}