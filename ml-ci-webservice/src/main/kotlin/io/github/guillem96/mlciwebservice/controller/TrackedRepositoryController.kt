package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.TrackedRepositoryRepository
import io.github.guillem96.mlciwebservice.findOne
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@RepositoryRestController
@RequestMapping("/trackedRepositories")
class TrackedRepositoryController(private val trackedRepositoryRepository: TrackedRepositoryRepository){

    @PostMapping("/{id}/incrementBuild")
    fun incrementBuildNumber(@PathVariable id: Long): ResponseEntity<Int> {
        trackedRepositoryRepository.findOne(id)?.let {
            it.buildNum++
            trackedRepositoryRepository.save(it)
            return ok(it.buildNum)
        }
        return notFound().build()
    }
}