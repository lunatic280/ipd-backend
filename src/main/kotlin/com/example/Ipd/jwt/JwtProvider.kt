package com.example.Ipd.jwt

import com.example.Ipd.user.service.CustomUserDetails
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))
    private val validity = 1000L * 60L * 60L * 24L // 24시간

    fun generateToken(userDetails: CustomUserDetails): String {
        val now = Date()
        val expiry = Date(now.time + validity)

        return Jwts.builder()
            .setSubject(userDetails.getId().toString())
            .claim("role", userDetails.getRole().name)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }

    fun getUserId(token: String): Long =
        Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token).body.subject.toLong()

    fun getRole(token: String): String =
        Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token).body["role"] as String
}
