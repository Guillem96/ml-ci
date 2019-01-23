package io.github.guillem96.mlciwebservice.service

open class StorageException(msg: String) : RuntimeException(msg)
class StorageFileNotFoundException(msg: String) : StorageException(msg)