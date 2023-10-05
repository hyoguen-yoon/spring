package com.fastcampus.springdatar2dbc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.r2dbc.ConnectionFactory
import org.springframework.boot.r2dbc.OptionsCapableConnectionFactory
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.sql.Connection

@SpringBootApplication
class FastcampusSpringWebfluxApplication{

    @Bean
    fun init(connectionFactory: ConnectionFactory)=
        ConnectionFactoryInitializer().apply{
            setConnetionFactory(connectionFactory)
            setDatabasePopulator(ResourceBatabasePoPulator(ClassPathResouce("script/schema.sql")))
        }

}

fun main(args: Array<String>) {
    runApplication<FastcampusSpringWebfluxApplication>(*args)
}
