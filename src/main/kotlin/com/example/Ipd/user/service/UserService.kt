package com.example.Ipd.user.service

import com.example.Ipd.security.UserRole
import com.example.Ipd.user.domain.User
import com.example.Ipd.user.dto.SignUpDto
import com.example.Ipd.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signUp(request: SignUpDto): Long {
        val encodePw = passwordEncoder.encode(request.password)

        val user = User(
            email = request.email,
            password = encodePw,
            name = request.name,
            // 전시본 회원가입은 일반 사용자만 생성합니다. 권한은 서버에서 결정합니다.
            role = UserRole.USER
        )
        return userRepository.save(user).id
    }

    fun login(email: String, password: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
        }
        return user
    }
}
