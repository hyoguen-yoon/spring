package com.fastcampus.springdatar2dbc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/books")
class BookController(    val bookRepository: BookRepository,)
{
    @GetMapping("{name}")
    fun getByName(@PathVariable name: String):Mono<Book>{
        return bookRepository.findByName(name)
    }

    @PostMapping
    fun create(@RequestBody map :Map<String,Any>) : Mono<Book>{
        var book= Book(
            name = map["name"].toString(),
            price =map["price"] as Int
        )

        return bookRepository.save(book)
    }
}