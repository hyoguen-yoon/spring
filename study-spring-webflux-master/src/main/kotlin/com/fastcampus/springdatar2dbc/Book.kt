package com.fastcampus.springdatar2dbc

import org.intellij.lang.annotations.Identifier

@Table
data class Book {
 @Id
 val id: Long? = null,
 @Column
  val name: String,
    @Column
    val parice: Int,


}