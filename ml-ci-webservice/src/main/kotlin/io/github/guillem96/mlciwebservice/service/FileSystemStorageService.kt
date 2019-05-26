package io.github.guillem96.mlciwebservice.service

import io.github.guillem96.mlciwebservice.StorageException
import io.github.guillem96.mlciwebservice.StorageFileNotFoundException
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class FileSystemStorageService(val environment: Environment) : StorageService {

    private val rootLocation: Path by lazy {
        Paths.get(environment.getRequiredProperty("models-path"))
    }

    override fun store(file: MultipartFile) {
        val filename = file.originalFilename
        try {
            // Check if file is null
            filename ?: throw StorageException("Failed to store null file")

            when {
                file.isEmpty -> throw StorageException("Failed to store empty file $filename")

                // This is a security check, no allow relative paths
                filename.contains("..") -> throw StorageException(
                        "Cannot store file with relative path outside current directory $filename")

                else -> {
                    file.inputStream.use { inputStream ->
                        Files.copy(inputStream, this.rootLocation.resolve(filename),
                                StandardCopyOption.REPLACE_EXISTING)
                    }
                }
            }
        } catch (e: IOException) {
            throw StorageException("Failed to store file $filename")
        }
    }

    override fun load(filename: String): Path = rootLocation.resolve(filename)

    override fun loadAsResource(filename: String): Resource {
        try {
            val file = load(filename)
            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable)
                return resource

            throw StorageFileNotFoundException("Could not read file: $filename")
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: $filename")
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