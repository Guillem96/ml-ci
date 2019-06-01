package io.github.guillem96.mlciwebservice.config

import org.apache.tomcat.jdbc.pool.DataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

import java.net.URI
import java.net.URISyntaxException

/**
 * Created by http://rhizomik.net/~roberto/
 */
@Configuration
@Profile("heroku | production")
class DatabaseConfig {
    @Bean
    @Throws(URISyntaxException::class)
    fun dataSource(): DataSource {
        val dbUri = URI(System.getenv("DATABASE_URL"))

        val username = dbUri.userInfo.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val password = dbUri.userInfo.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val dbUrl = "jdbc:postgresql://${dbUri.host}:${dbUri.port}${dbUri.path}"
        val basicDataSource = DataSource()
        basicDataSource.url = dbUrl
        basicDataSource.username = username
        basicDataSource.password = password

        return basicDataSource
    }
}