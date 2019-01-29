package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.*
import io.github.guillem96.mlciwebservice.domain.Model
import io.github.guillem96.mlciwebservice.domain.ModelStatus
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RepositoryRestController
@RequestMapping("/models")
class ModelController(
        private val modelRepository: ModelRepository,
        private val trackedRepositoryRepo: TrackedRepositoryRepository
) {

    /**
     * Creates a model linked with a tracked repository in a single shot
     * Returns the id associated with the new model
     */
    @PostMapping("/withTrackedRepository")
    fun createModel(@RequestBody data: ModelWithRepo): ResponseEntity<Long> {
        // Get repository or return 404
        trackedRepositoryRepo.findOne(data.trackedRepository)?.let {
            val model = Model(algorithm = data.algorithm,
                    hyperParameters = data.hyperParameters,
                    status = data.status,
                    buildNum = it.buildNum, // Build number is the same as the current buildNum of the trackedRepository
                    trackedRepository = it)
            modelRepository.save(model)
            return ok(model.id!!)
        }

        return notFound().build()
    }

    /**
     * Update the model status
     * Returns the new status
     */
    @PostMapping("{id}/status/{status}")
    fun updateModelStatus(@PathVariable("id") modelId: Long,
                          @PathVariable("status") status: ModelStatus): ResponseEntity<ModelStatus> {
        modelRepository.findOne(modelId)?.let {
            it.status = status
            modelRepository.save(it)
            return ok(status)
        }

        return notFound().build()
    }

    /**
     * Add evaluations to a model after its training
     * */
    @PostMapping("{id}/evaluations/")
    fun updateEvaluations(@PathVariable("id") modelId: Long,
                          @RequestBody evaluations: Map<String, Double>): ResponseEntity<Any> {
        modelRepository.findOne(modelId)?.let {
            it.evaluations.clear()
            it.evaluations.putAll(evaluations)
            modelRepository.save(it)
            return ok(it.evaluations)
        }

        return notFound().build()
    }
}

data class ModelWithRepo(
        val algorithm: String,
        val hyperParameters: Map<String, Any>,
        val status: ModelStatus,
        val trackedRepository: Long)