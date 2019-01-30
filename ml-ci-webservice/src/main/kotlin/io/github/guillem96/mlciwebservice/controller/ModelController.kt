package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.*
import io.github.guillem96.mlciwebservice.domain.Model
import io.github.guillem96.mlciwebservice.domain.ModelStatus
import org.springframework.data.rest.webmvc.PersistentEntityResource
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler
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
     * Returns the new model
     */
    @PostMapping("/withTrackedRepository")
    fun createModel(@RequestBody data: ModelWithRepo,
                    resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<PersistentEntityResource> {
        // Get repository or return 404
        trackedRepositoryRepo.findOne(data.trackedRepository)?.let {
            val model = Model(algorithm = data.algorithm,
                    hyperParameters = data.hyperParameters,
                    status = data.status,
                    buildNum = it.buildNum, // Build number is the same as the current buildNum of the trackedRepository
                    trackedRepository = it)
            modelRepository.save(model)
            return ok(resourceAssembler.toResource(model))
        }
        return notFound().build()
    }

    /**
     * Update the model status
     * Returns the new status
     */
    @PostMapping("{id}/status/{status}")
    fun updateModelStatus(@PathVariable("id") modelId: Long,
                          @PathVariable status: ModelStatus,
                          resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<PersistentEntityResource> {
        modelRepository.findOne(modelId)?.let {
            it.status = status
            modelRepository.save(it)
            return ok(resourceAssembler.toResource(it))
        }

        return notFound().build()
    }

    /**
     * Add evaluations to a model after its training
     * */
    @PostMapping("{id}/evaluations/")
    fun updateEvaluations(@PathVariable("id") modelId: Long,
                          @RequestBody evaluations: Map<String, Double>,
                          resourceAssembler: PersistentEntityResourceAssembler): ResponseEntity<PersistentEntityResource> {
        modelRepository.findOne(modelId)?.let {
            it.evaluations.putAll(evaluations)
            modelRepository.save(it)
            return ok(resourceAssembler.toResource(it))
        }
        return notFound().build()
    }
}

data class ModelWithRepo(
        val algorithm: String,
        val hyperParameters: Map<String, Any>,
        val status: ModelStatus,
        val trackedRepository: Long)