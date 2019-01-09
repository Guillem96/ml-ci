package io.github.guillem96.mlciwebservice

import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
class AuthenticationTest {

    @Autowired
    private lateinit var wac: WebApplicationContext

    @Autowired
    private lateinit var userRepository: UserRepository

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders
                .webAppContextSetup(wac)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    @Test
    fun fail_on_post_without_authentication() {
         mockMvc.perform(post("/records"))
                 .andExpect(status().isForbidden)
    }

    @Test
    fun sign_up_and_sign_in() {
        signUp(mockMvc, "guillem", "password")
                .andExpect(status().isCreated)

        signIn(mockMvc, "guillem", "password")
                .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun invalid_signup_without_password() {
        signUp(mockMvc, "guillem", "")
                .andExpect(status().is4xxClientError)
    }
}