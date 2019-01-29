package io.github.guillem96.mlciwebservice

import io.github.guillem96.mlciwebservice.domain.User
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
@RepositoryEventHandler
class UserEventHandler {
    @HandleBeforeCreate
    @Transactional
    fun handleUserPostCreate(user: User) {
        user.encodePassword()
    }
}