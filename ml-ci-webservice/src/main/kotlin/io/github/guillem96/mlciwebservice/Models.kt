package io.github.guillem96.mlciwebservice

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
data class Record(
        @NotBlank val title: String,
        @Id @GeneratedValue val id: Long? = null)