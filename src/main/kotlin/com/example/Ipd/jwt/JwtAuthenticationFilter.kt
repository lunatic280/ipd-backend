package com.example.Ipd.jwt

import com.example.Ipd.user.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)

            if (jwtProvider.validateToken(token)) {
                try {
                    val userId = jwtProvider.getUserId(token)
                    val role = jwtProvider.getRole(token) // 필요하면 사용

                    val userDetails = userDetailsService.loadUserById(userId)

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )

                    authentication.details =
                        WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                } catch (e: UsernameNotFoundException) {
                    // 토큰에는 id가 있는데 DB에 유저가 없는 경우 → 토큰은 더 이상 유효하지 않음
                    SecurityContextHolder.clearContext()
                    // 여기서 바로 401을 내려도 되고(선택),
                    // 지금은 그냥 인증 없이 다음 필터로 넘겨서
                    // Security 설정에 따라 401/403 처리되게 둡니다.
                } catch (e: Exception) {
                    // JWT 파싱이나 다른 문제 발생 시에도 인증은 설정하지 않고 로그만 남김
                    SecurityContextHolder.clearContext()
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
