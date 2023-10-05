package com.fastcampus.userservice.controller

import com.fastcampus.userservice.model.*
import com.fastcampus.userservice.service.UserService
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.io.File


@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SingUpRequest){
        userService.singUp(request)
    }

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody singInRequest: SingInRequest) :SingInResponse{
      return userService.signIn(singInRequest)
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken token:String) {
        return userService.logout(token)
    }

    @GetMapping("/me")
    suspend fun get(
        @AuthToken token: String
    ): MeResponse {
        return MeResponse(userService.getByToken(token))
    }
    @GetMapping("/{userId}/username")
    suspend fun getUsername(@PathVariable userId: Long): Map<String, String> {
        return mapOf("reporter" to userService.get(userId).username)
    }


    @PostMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun edit(
        @PathVariable id: Long,
        @ModelAttribute request: UserEditRequest,
        @AuthToken token: String,
        @RequestPart("profileUrl") filePart: FilePart,
    ) {
        val orgFilename = filePart.filename()
        var filename: String? = null
        if (orgFilename.isNotEmpty()) {
            val ext = orgFilename.substring(orgFilename.lastIndexOf(".") + 1)
            filename = "${id}.${ext}"

            //resources/images/1.jpg
            val file = File(ClassPathResource("/images/").file, filename)
            filePart.transferTo(file).awaitSingleOrNull()
        }
        userService.edit(token, request.username, filename)
    }

}