 package io.github.guillem96.mlciwebservice

import io.github.guillem96.mlciwebservice.domain.Approach
import io.github.guillem96.mlciwebservice.domain.TrackedRepository
import io.github.guillem96.mlciwebservice.domain.User
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
interface ApproachRepository: CrudRepository<Approach, Long>


// CrudRepository extension to use kotlin null safe advantage
fun <T, ID> CrudRepository<T, ID>.findOne(id: ID): T? = findById(id).orElse(null)