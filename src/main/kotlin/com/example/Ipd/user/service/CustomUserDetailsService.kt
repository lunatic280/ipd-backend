package com.example.Ipd.user.service

import com.example.Ipd.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        val user = username?.let { userRepository.findByEmail(it) }
            ?: throw UsernameNotFoundException("user not found: $username")
        return CustomUserDetails(user)
    }

    fun loadUserById(id: Long): UserDetails {
        val user = userRepository.findById(id)
            .orElseThrow { UsernameNotFoundException("user not found: $id") }
        return CustomUserDetails(user)
    }
}
