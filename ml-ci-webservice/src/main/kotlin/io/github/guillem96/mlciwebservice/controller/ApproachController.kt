package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.ApproachRepository
import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.domain.Approach
import io.github.guillem96.mlciwebservice.domain.ApproachStatus
import io.github.guillem96.mlciwebservice.findOne
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/approaches")
class ModelController(
        private val approachRepository: ApproachRepository,
        private val trackedRepositoryRepo: TrackedRepositoryRepository
) {

    /**
     * Creates a model linked with a tracked repository in a single shot
     * Returns the id associated with the new model
     */
    @PostMapping("/withTrackedRepository")
    fun createApproach(@RequestBody data: ApproachWithRepo): ResponseEntity<Long> {
        // Get repository or return 404
        trackedRepositoryRepo.findOne(data.trackedRepository)?.let {
            val model = Approach(name = data.name,
                    status = data.status,
                    buildNum = it.buildNum, // Build number is the same as the current buildNum of the trackedRepository
                    trackedRepository = it)
            approachRepository.save(model)
            return ok(model.id!!)
        }

        return notFound().build()
    }

    /**
     * Update the model status
     * Returns the new status
     */
    @PostMapping("{id}/status/{status}")
    fun updateApproachStatus(@PathVariable("id") modelId: Long,
                             @PathVariable("status") status: ApproachStatus): ResponseEntity<ApproachStatus> {
        approachRepository.findOne(modelId)?.let {
            it.status = status
            approachRepository.save(it)
            return ok(status)
        }

        return notFound().build()
    }

}

data class ApproachWithRepo(
        val name: String,
        val status: ApproachStatus,
        val trackedRepository: Long)