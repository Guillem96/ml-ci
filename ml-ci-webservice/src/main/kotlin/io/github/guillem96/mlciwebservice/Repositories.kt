package io.github.guillem96.mlciwebservice

import org.springframework.data.repository.CrudRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface RecordRepository: CrudRepository<Record, Long>