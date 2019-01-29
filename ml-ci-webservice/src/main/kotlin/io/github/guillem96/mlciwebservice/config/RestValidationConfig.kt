package io.github.guillem96.mlciwebservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.context.annotation.Primary
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer


@Configuration
class RestValidationConfig : RepositoryRestConfigurer {

    @Bean
    @Primary
    internal fun validator(): LocalValidatorFactoryBean {
        return LocalValidatorFactoryBean()
    }

    override fun configureValidatingRepositoryEventListener(
            validatingListener: ValidatingRepositoryEventListener?) {

        validatingListener?.let {
            it.addValidator("beforeCreate", validator())
            it.addValidator("beforeSave", validator())
            super.configureValidatingRepositoryEventListener(validatingListener)
        }
    }
}