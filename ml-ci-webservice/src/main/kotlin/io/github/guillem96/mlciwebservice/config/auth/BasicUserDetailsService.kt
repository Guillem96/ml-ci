package io.github.guillem96.mlciwebservice.config.auth

import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component


//@Component
//class BasicUserDetailsService : UserDetailsService {
//
////    @Autowired
////    internal var userRepository: UserRepository? = null
//
//    @Throws(UsernameNotFoundException::class)
//    override fun loadUserByUsername(username: String): UserDetails {
//        return userRepository!!.findByUsername(username) ?: throw UsernameNotFoundException("Incorrect email")
//    }
//}