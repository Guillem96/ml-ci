package io.github.guillem96.mlciwebservice.controller

import io.github.guillem96.mlciwebservice.service.StorageService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@Controller
@RequestMapping("/static/evaluations")
class EvaluationUploaderController(private val storageService: StorageService) {

    /**
     * Get and return an static file
     */
    @GetMapping("/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Any> {
        val file = storageService.loadAsResource(filename)
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.filename + "\"").body<Any>(file)
    }

    /**
     * Upload a new static file
     * */
    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun handleFileUpload(@RequestParam("file") file: MultipartFile) {
        storageService.store(file)
    }
}