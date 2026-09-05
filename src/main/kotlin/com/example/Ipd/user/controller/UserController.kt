package com.example.Ipd.user.controller

import com.example.Ipd.jwt.JwtProvider
import com.example.Ipd.user.dto.LoginDto
import com.example.Ipd.user.dto.SignUpDto
import com.example.Ipd.user.dto.AuthDto
import com.example.Ipd.user.service.CustomUserDetails
import com.example.Ipd.user.service.UserService
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest

import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class UserController(
    private val userService: UserService,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpDto): ResponseEntity<Long> {
        val userId = userService.signUp(request)
        return runCatching {
            ResponseEntity.ok(userId)
        }.getOrElse {
            ResponseEntity.badRequest().build()
        }

    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginDto,
        http: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val user = userService.login(request.email, request.password)
            val userDetails = CustomUserDetails(user)
            val token = jwtProvider.generateToken(userDetails)

            ResponseEntity.ok(
                mapOf(
                    "token" to token,
                    "userId" to user.id,
                    "role" to user.role.name
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }
}
