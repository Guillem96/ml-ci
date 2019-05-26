package io.github.guillem96.mlciwebservice

import io.github.guillem96.mlciwebservice.service.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class MlCiWebserviceApplication {
    @Bean
    fun init(storageService: StorageService) = CommandLineRunner {
        storageService.deleteAll()
        storageService.init()
    }
}

fun main(args: Array<String>) {
	runApplication<MlCiWebserviceApplication>(*args)
}


