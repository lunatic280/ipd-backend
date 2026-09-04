package com.example.Ipd.user.dto

import com.example.Ipd.security.UserRole

data class AuthDto(
    val userId: Long,
    val role: UserRole
)
