package io.github.guillem96.mlciwebservice.service


import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream

import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile

@Service
class FileSystemStorageService(val environment: Environment) : StorageService {

    private val rootLocation: Path by lazy {
        Paths.get(environment.getRequiredProperty("models-path"))
    }

    override fun store(file: MultipartFile) {
        val filename = file.originalFilename
        try {
            filename ?: throw StorageException("Failed to store null file")

            if (file.isEmpty) {
                throw StorageException("Failed to store empty file " + filename)
            }

            if (filename.contains("..")) {
                // This is a security check
                throw StorageException(
                        "Cannot store file with relative path outside current directory " + filename)
            }

            file.inputStream.use { inputStream ->
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw StorageException("Failed to store file " + filename)
        }

    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        try {
            val file = load(filename)
            val resource = UrlResource(file.toUri())
            return if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException(
                        "Could not read file: " + filename)

            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: " + filename)
        }

    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage")
        }
    }
}