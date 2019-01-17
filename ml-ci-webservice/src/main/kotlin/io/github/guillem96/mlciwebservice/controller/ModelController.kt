package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.*
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RepositoryRestController
@RequestMapping("/models")
class ModelsController(
        private val modelRepository: ModelRepository,
        private val trackedRepositoryRepo: TrackedRepositoryRepository
) {

    @PostMapping("/withTrackedRepository")
    fun createModel(@RequestBody data: ModelWithRepo): ResponseEntity<Long> {
        trackedRepositoryRepo.findOne(data.trackedRepository)?.let {
            val model = Model(algorithm = data.algorithm,
                    hyperParameters = data.hyperParameters,
                    status = data.status,
                    trackedRepository = it)
            modelRepository.save(model)
            return ok(model.id!!)
        }

        return notFound().build()
    }

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
}

data class ModelWithRepo(
        val algorithm: String,
        val hyperParameters: Map<String, Any>,
        val status: ModelStatus,
        val trackedRepository: Long)