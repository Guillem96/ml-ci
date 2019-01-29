package io.github.guillem96.mlciwebservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

// Authentication exceptions
class InvalidJwtAuthenticationException(message:String): Exception(message)

// Storage service exceptions
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
open class StorageException(msg: String) : RuntimeException(msg)

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Specified file not found")
class StorageFileNotFoundException(msg: String) : StorageException(msg)