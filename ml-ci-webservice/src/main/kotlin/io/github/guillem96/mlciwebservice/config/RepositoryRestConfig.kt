package io.github.guillem96.mlciwebservice.config

import io.github.guillem96.mlciwebservice.Record
import io.github.guillem96.mlciwebservice.RecordRepository
import io.github.guillem96.mlciwebservice.User
import javax.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter

@Configuration
class RepositoryRestConfig(private val environment: Environment,
                           private val recordRepository: RecordRepository) : RepositoryRestConfigurerAdapter() {

    @Override
    override fun configureRepositoryRestConfiguration(config: RepositoryRestConfiguration) {
        config.exposeIdsFor(Record::class.java)
        config.exposeIdsFor(User::class.java)
    }

    @PostConstruct
    fun init() {
        if(!environment.activeProfiles.contains("Test")) {
            recordRepository.save(Record("Record1"))
        }
    }
}