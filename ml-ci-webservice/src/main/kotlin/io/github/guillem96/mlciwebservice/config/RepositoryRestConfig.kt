package io.github.guillem96.mlciwebservice.config

import io.github.guillem96.mlciwebservice.*
import io.github.guillem96.mlciwebservice.domain.*
import javax.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class RepositoryRestConfig(private val environment: Environment,
                           private val userRepository: UserRepository,
                           private val trackedRepositoryRepository: TrackedRepositoryRepository,
                           private val modelRepository: ModelRepository,
                           private val evaluationRepository: EvaluationRepository) : RepositoryRestConfigurerAdapter() {

    @Override
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(TrackedRepository::class.java)
        config.exposeIdsFor(Model::class.java)
        config.exposeIdsFor(Evaluation::class.java)
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
                        url = "https://github.com/Guillem96/ml-ci-test",
                        lastCommit = "5f792244a94136c418644ca60f7359475b7db831",
                        user = user)
                trackedRepositoryRepository.save(trackedRepository)

                val model = Model(algorithm = "LinearRegression",
                        trackedRepository = trackedRepository,
                        hyperParameters = mapOf("alpha" to 0.1))
                modelRepository.save(model)

                val evaluation = Evaluation(model = model, results = mapOf("accuracy" to 90.6))
                evaluationRepository.save(evaluation)

                val trackedRepository2 = TrackedRepository(
                        url = "https://github.com/Guillem96/argon-nx",
                        lastCommit = "5f792244a94136c418644ca60f7359475b7db831",
                        user = user)
                trackedRepositoryRepository.save(trackedRepository2)


                modelRepository.save(Model(algorithm = "LinearRegression",
                        trackedRepository = trackedRepository2,
                        hyperParameters = mapOf("alpha" to 0.2)))

                modelRepository.save(Model(algorithm = "LogisticRegression",
                        trackedRepository = trackedRepository2,
                        status = ModelStatus.ERROR,
                        hyperParameters = mapOf("alpha" to 0.2)))
            }
        }
    }
}