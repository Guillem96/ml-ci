package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.service.StorageException
import org.springframework.http.ResponseEntity
import io.github.guillem96.mlciwebservice.service.StorageFileNotFoundException
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.HttpHeaders
import io.github.guillem96.mlciwebservice.service.StorageService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/static/models")
class ModelUploaderController(private val storageService: StorageService) {

    @GetMapping("/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Any> {

        val file = storageService.loadAsResource(filename)
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.filename + "\"").body<Any>(file)
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun handleFileUpload(@RequestParam("file") file: MultipartFile) {
        storageService.store(file)
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(exc: StorageFileNotFoundException): ResponseEntity<*> {
        return ResponseEntity.notFound().build<Any>()
    }

    @ExceptionHandler(StorageException::class)
    fun handleStorageError(exc: StorageException): ResponseEntity<*> {
        return ResponseEntity.badRequest().build<Any>()
    }
}