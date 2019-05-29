package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.LogsRepository
import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.findOne
import io.github.guillem96.mlciwebservice.domain.ApproachStatus
import io.github.guillem96.mlciwebservice.domain.LogLevel
import io.github.guillem96.mlciwebservice.domain.MlCiLog

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*


@RepositoryRestController
@RequestMapping("/trackedRepositories")
class TrackedRepositoryController(
        private val trackedRepositoryRepository: TrackedRepositoryRepository,
        private val logsRepository: LogsRepository,
        private val rabbitTemplate: RabbitTemplate,
        private val queue: Queue){
    
    @PostMapping("{id}/status/{status}")
    fun updateTrackedRepoStatus(@PathVariable("id") id: Long,
                                @PathVariable("status") status: ApproachStatus): ResponseEntity<ApproachStatus> {
        trackedRepositoryRepository.findOne(id)?.let {
            it.status = status
            trackedRepositoryRepository.save(it)
            return ok(status)
        }

        return notFound().build()
    }

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

    /**
     * List all logs corresponding to a build number
     */
    @GetMapping("/{id}/logs/{buildNum}")
    fun findLogsByBuildNum(@PathVariable id: Long,
                           @PathVariable buildNum: Int): ResponseEntity<List<MlCiLogJSON>>{
        trackedRepositoryRepository.findOne(id)?.let {
            val logs = logsRepository
                        .findByTrackedRepository(it)
                        .filter { log -> log.buidNum == buildNum }
                        .map { log -> MlCiLogJSON(log.level, log.message) }
            return ok(logs)
        }
        return notFound().build()
    }

    /**
     * Create new log
     */
    @PostMapping("/{id}/log")
    fun log(@PathVariable("id") id: Long,
            @RequestBody log: MlCiLogJSON): ResponseEntity<Int> {

        trackedRepositoryRepository.findOne(id)?.let {
            val newLog = MlCiLog(level = log.level,
                    message = log.message,
                    trackedRepository = it,
                    buidNum = it.buildNum)
            logsRepository.save(newLog)
            return ok().build()
        }

        return notFound().build()
    }
}

data class MlCiLogJSON(val level: LogLevel, val message: String)