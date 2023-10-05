package com.fastcampus.userservice.utils

import com.auth0.jwt.interfaces.DecodedJWT
import com.fastcampus.userservice.config.JWTProperties
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JWTUtilsTest {

    private val logger =KotlinLogging.logger{}

    @Test
    fun createTokenTest(){
        val jwtClaim = JWTClaim(
            userId = 1,
            email = "test@test.com",
            profileUrl = "asd.jpg",
            username = "asd"
        )

        val properties = JWTProperties(
            issuer = "jara",
            subject = "auth",
            expiresTime = 3600,
            secret = "my-secret"
        )

        val token =    JWTUtils.createToken(jwtClaim,properties)
   //    assertNotNull(token)

        logger.info { "token : $token" }
    }

    @Test
    fun decodeTest(){
        val jwtClaim = JWTClaim(
            userId = 1,
            email = "test@test.com",
            profileUrl = "asd.jpg",
            username = "asd"
        )

        val properties = JWTProperties(
            issuer = "jara",
            subject = "auth",
            expiresTime = 3600,
            secret = "my-secret"
        )

        val token =    JWTUtils.createToken(jwtClaim,properties)
        //    assertNotNull(token)
        val decode : DecodedJWT=JWTUtils.decode(token, secret = properties.secret, issuer = properties.issuer)


        with(decode){
            logger.info { "claims : $claims" }
            val userId = claims["userId"]!!.asLong()
            Assertions.assertEquals(userId,jwtClaim.userId)
            val email = claims["email"]!!.asString()
            Assertions.assertEquals(email,jwtClaim.email)
            val profileUrl = claims["profileUrl"]!!.asString()
            Assertions.assertEquals(profileUrl,jwtClaim.profileUrl)
            val username = claims["username"]!!.asString()
            Assertions.assertEquals(username,jwtClaim.username)




        }




    }


}