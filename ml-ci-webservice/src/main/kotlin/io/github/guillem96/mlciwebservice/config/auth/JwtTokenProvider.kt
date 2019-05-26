package io.github.guillem96.mlciwebservice.config.auth

import io.github.guillem96.mlciwebservice.InvalidJwtAuthenticationException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.core.env.Environment
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider(
        private val environment: Environment,
        private val userDetailsService: BasicUserDetailsService) {

    private val secretKey: String by lazy {
        Base64.getEncoder().encodeToString(
                environment.getRequiredProperty("security.jwt.token.secret-key").toByteArray())
    }

    private val validityInMilliseconds: Int by lazy {
        environment.getRequiredProperty("security.jwt.token.expire-length").toInt()
    }

    fun createToken(username: String, roles: List<String>): String {

        val claims = Jwts.claims().setSubject(username)
        claims["roles"] = roles

        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private fun getUsername(token: String): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken: String? = req.getHeader("Authorization")
        return bearerToken?.let {
            if (it.startsWith("Bearer ")) {
                return it.substring(7)
            }
            return null
        }
    }

    @Throws(InvalidJwtAuthenticationException::class)
    fun validateToken(token: String): Boolean {
        try {
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.body.expiration.before(Date())
        } catch (e: Exception) {
            throw InvalidJwtAuthenticationException("Invalid token")
        }
    }
}