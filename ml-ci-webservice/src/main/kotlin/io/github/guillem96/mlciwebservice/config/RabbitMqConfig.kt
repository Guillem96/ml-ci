package io.github.guillem96.mlciwebservice.config

import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.URI

@Configuration
class RabbitMqConfig(private val environment: Environment) {
    @Bean
    fun connectionFactory(): ConnectionFactory =
            CachingConnectionFactory(URI(environment.getRequiredProperty("rabbit-mq-url")))

    @Bean
    fun amqpAdmin(): AmqpAdmin = RabbitAdmin(connectionFactory())

    @Bean
    fun rabbitTemplate(): RabbitTemplate = RabbitTemplate(connectionFactory())

    @Bean
    fun trainRepos() : Queue = Queue("train_repos", false)
}