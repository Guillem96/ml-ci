package io.github.guillem96.mlciwebservice.service


import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface StorageService {
    fun init()
    fun store(file: MultipartFile)
    fun load(filename: String): Path
    fun loadAsResource(filename: String): Resource
    fun deleteAll()
}