package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.findOne
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/trackedRepositories")
class TrackedRepositoryController(
        private val trackedRepositoryRepository: TrackedRepositoryRepository,
        private val rabbitTemplate: RabbitTemplate,
        private val queue: Queue){

    /**
     * Increment number of trainings
     */
    @PostMapping("/{id}/incrementBuild")
    fun incrementBuildNumber(@PathVariable id: Long): ResponseEntity<Int> {
        trackedRepositoryRepository.findOne(id)?.let {
            it.buildNum++
            trackedRepositoryRepository.save(it)
            return ok(it.buildNum)
        }
        return notFound().build()
    }

    /**
     * Publish a training task to rabbit mq queue
     */
    @PostMapping("/{id}/train")
    fun trainRepository(@PathVariable id: Long): ResponseEntity<Unit> {
        trackedRepositoryRepository.findOne(id)?.let {
            val message = """
            {
                "trackedRepositoryId" : ${it.id},
                "githubUrl": "${it.url}",
                "githubToken": ""
            }
            """
            this.rabbitTemplate.convertAndSend(queue.name, message)
            return ok().build()
        }
        return notFound().build()
    }
}