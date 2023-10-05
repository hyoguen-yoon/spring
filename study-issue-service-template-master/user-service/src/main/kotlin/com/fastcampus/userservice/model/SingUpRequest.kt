package com.fastcampus.userservice.model

data class SingUpRequest(
    val email: String,
    val password : String,
    val username : String,

)

data class SingInRequest(
    val email: String,
    val password : String,
    val username : String,

    )

data class SingInResponse(
    val email: String,
    val username : String,
    val token: String
    )
