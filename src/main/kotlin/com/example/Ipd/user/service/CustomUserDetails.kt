package com.example.Ipd.user.service

import com.example.Ipd.security.UserRole
import com.example.Ipd.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
): UserDetails {

    fun getId(): Long = user.id
    fun getRole(): UserRole = user.role
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority("ROLE_${user.role}"))

    override fun getPassword(): String = user.password
    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
