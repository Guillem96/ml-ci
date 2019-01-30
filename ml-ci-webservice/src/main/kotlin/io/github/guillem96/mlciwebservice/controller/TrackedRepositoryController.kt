package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.LogRepository
import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.domain.Log
import io.github.guillem96.mlciwebservice.domain.LogLevel
import io.github.guillem96.mlciwebservice.findOne
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.rest.webmvc.PersistentEntityResource
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/trackedRepositories")
class TrackedRepositoryController(
        private val trackedRepositoryRepository: TrackedRepositoryRepository,
        private val logRepository: LogRepository,
        private val rabbitTemplate: RabbitTemplate,
        private val queue: Queue) {

    /**
     * Increment number of trainings
     */
    @PostMapping("/{id}/incrementBuild")
    fun incrementBuildNumber(@PathVariable id: Long,
                             resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<PersistentEntityResource> {
        trackedRepositoryRepository.findOne(id)?.let {
            it.buildNum++
            trackedRepositoryRepository.save(it)
            return ok(resourceAssembler.toResource(it))
        }
        return notFound().build()
    }

    /**
     * Generate new log
     */
    @PostMapping("/{id}/log")
    fun appendLog(
            @PathVariable id: Long,
            @RequestBody log: LogJson,
            resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<PersistentEntityResource> {
        trackedRepositoryRepository.findOne(id)?.let {
            val newLog: Log = Log(
                    message = log.message,
                    logLevel = log.logLevel,
                    buildNum = it.buildNum,
                    trackedRepository = it
            )
            logRepository.save(newLog)
            return ok(resourceAssembler.toResource(newLog))
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

data class LogJson(val message: String, val logLevel: LogLevel)