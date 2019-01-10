package io.github.guillem96.mlciwebservice

import org.springframework.data.rest.core.annotation.HandleAfterCreate
import org.springframework.data.rest.core.annotation.HandleBeforeCreate
import org.springframework.data.rest.core.annotation.RepositoryEventHandler
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
@RepositoryEventHandler
class UserEventHandler(private val userRepository: UserRepository) {

    @HandleBeforeCreate
    @Transactional
    fun handleUserPostCreate(user: User) {
        user.encodePassword()
    }
}