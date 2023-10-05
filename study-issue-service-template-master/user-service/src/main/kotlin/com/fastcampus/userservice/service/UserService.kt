package com.fastcampus.userservice.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.fastcampus.userservice.config.JWTProperties
import com.fastcampus.userservice.domain.entity.User
import com.fastcampus.userservice.domain.repository.UserRepository
import com.fastcampus.userservice.exception.InvalidJwtTokenException
import com.fastcampus.userservice.exception.PasswordNotMatchedException
import com.fastcampus.userservice.exception.UserExitsException
import com.fastcampus.userservice.exception.UserNotFoundException
import com.fastcampus.userservice.model.SingInRequest
import com.fastcampus.userservice.model.SingInResponse
import com.fastcampus.userservice.model.SingUpRequest
import com.fastcampus.userservice.utils.BCryptUtils
import com.fastcampus.userservice.utils.JWTClaim
import com.fastcampus.userservice.utils.JWTUtils
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtProperties: JWTProperties,
    private val ccheManager: CoroutineCcheManager<User>
) {
    companion object {
        private val CACHE_TTL = Duration.ofMillis(1)
    }


    suspend fun singUp(singUpRequest: SingUpRequest) {
        with(singUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExitsException()
            }
            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username = username,
            )
            userRepository.save(user)

        }


    }

    suspend fun signIn(signInrequest: SingInRequest): SingInResponse {
        return with(userRepository.findByEmail(signInrequest.email) ?: throw UserNotFoundException()) {
            val verified = BCryptUtils.verify(signInrequest.password, password)
            if (!verified) {
                throw PasswordNotMatchedException()
            }

            val jwtClaim = JWTClaim(
                userId = id!!,
                email = email,
                profileUrl = profileUrl,
                username = username,
            )

            val token = JWTUtils.createToken(jwtClaim, jwtProperties)
            ccheManager.awaitPut(key = token, value = this, ttl = CACHE_TTL)
            SingInResponse(
                email = email,
                username = username,
                token = token
            )
        }

    }

    suspend fun logout(token: String) {
        ccheManager.awaitEvict(token)
    }

    suspend fun getByToken(token: String): User {
        val cachedUser = ccheManager.awaitGetOrPut(key = token, ttl = CACHE_TTL) {
            // 캐시가 유효하지 않은 경우 동작
            val decodedJWT: DecodedJWT = JWTUtils.decode(token, jwtProperties.secret, jwtProperties.issuer)

            val userId: Long = decodedJWT.claims["userId"]?.asLong() ?: throw InvalidJwtTokenException()
            get(userId)
        }
        return cachedUser
    }

    suspend fun get(userId: Long): User {
        return userRepository.findById(userId) ?: throw UserNotFoundException()
    }

    suspend fun edit(token: String, username: String, filename: String?) {
        val user = getByToken(token)

        val newUser = user.copy(username = username, profileUrl = profileUrl ?: user.profileUrl)
        return userRepository.save(newUser).also {
            cacheManager.awaitPut(key = token, value = it, ttl = CACHE_TTL)
        }
    }
}