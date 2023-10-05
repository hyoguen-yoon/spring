package com.fastcampus.springdatar2dbc

interface BookRepository :ReactiveCrudRepository<Book,Long>{
    fun findByName(name: String) : Mono<Book>
}