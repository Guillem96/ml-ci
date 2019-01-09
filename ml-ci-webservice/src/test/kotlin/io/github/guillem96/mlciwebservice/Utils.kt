package io.github.guillem96.mlciwebservice

import org.json.JSONObject
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

fun signUp(server: MockMvc, username: String, password: String): ResultActions {
    val user = JSONObject()
    user.put("username", username)
    user.put("password", password)

    return server.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(user.toString())
            .accept(MediaType.APPLICATION_JSON_UTF8))
}


fun signIn(server: MockMvc, username: String, password: String): ResultActions {
    val user = JSONObject()
    user.put("username", username)
    user.put("password", password)

    return server.perform(MockMvcRequestBuilders.post("/auth/signIn")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(user.toString())
            .accept(MediaType.APPLICATION_JSON_UTF8))
}