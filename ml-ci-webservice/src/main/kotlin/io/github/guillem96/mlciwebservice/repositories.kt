package io.github.guillem96.mlciwebservice

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface TrackedRepositoryRepository: CrudRepository<TrackedRepository, Long>

@RepositoryRestResource
interface UserRepository: CrudRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}

@RepositoryRestResource
interface ModelRepository: CrudRepository<Model, Long>

@RepositoryRestResource
interface EvaluationRepository: CrudRepository<Evaluation, Long>


// Repo extension to use kotlin null safe
fun <T, ID> CrudRepository<T, ID>.findOne(id: ID): T? = findById(id).orElse(null)