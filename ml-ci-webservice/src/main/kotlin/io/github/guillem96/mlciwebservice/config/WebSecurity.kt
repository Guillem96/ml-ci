package io.github.guillem96.mlciwebservice.config

import io.github.guillem96.mlciwebservice.config.auth.JwtConfigurer
import io.github.guillem96.mlciwebservice.config.auth.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class WebSecurity(
        private val jwtTokenProvider: JwtTokenProvider,
        private val environment: Environment) : WebSecurityConfigurerAdapter() {


    @Bean
    override fun authenticationManagerBean() = super.authenticationManagerBean()

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.POST,"/auth/signIn").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()

                .antMatchers(HttpMethod.GET, "/static/models/**").authenticated()
                .antMatchers(HttpMethod.POST, "/static/models*/**").hasRole("MODULE")

                .antMatchers(HttpMethod.POST, "/models*/**").hasRole("MODULE")
                .antMatchers(HttpMethod.POST, "/trackedRepositories/**/incrementBuild").hasRole("MODULE")

                .antMatchers(HttpMethod.GET, "**").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .apply(JwtConfigurer(jwtTokenProvider))
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = listOf("*")
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}