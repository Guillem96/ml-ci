package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.InvalidJwtAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

class JwtTokenFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filterChain: FilterChain) {
        try {
            jwtTokenProvider.resolveToken(req as HttpServletRequest)?.let {
                if (jwtTokenProvider.validateToken(it)) {
                    val auth = jwtTokenProvider.getAuthentication(it)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
            filterChain.doFilter(req, res)
        } catch (e: InvalidJwtAuthenticationException) {
            (res as HttpServletResponse).status = HttpStatus.UNAUTHORIZED.value()
            res.getWriter().write(HttpStatus.UNAUTHORIZED.reasonPhrase)
        }
    }
}