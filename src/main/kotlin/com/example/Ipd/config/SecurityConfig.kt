package com.example.Ipd.config

import com.example.Ipd.jwt.JwtAuthenticationFilter
import com.example.Ipd.user.service.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login").permitAll()
                // 전시본: 장치 제어와 센서 수신은 실제 권한 검증이 추가될 때까지 차단합니다.
                it.requestMatchers("/qr/**", "/led/**", "/test").denyAll()
                it.requestMatchers("/directions", "/get").authenticated()
                it.requestMatchers("/images/**", "/uploads/**").permitAll()
                it.requestMatchers(HttpMethod.GET, "/comment/**").permitAll()

                // PLACE & MENU: writes OWNER-only, reads USER/OWNER
                it.requestMatchers(HttpMethod.POST, "/place/**", "/menus/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.PUT, "/place/**", "/menus/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.PATCH, "/place/**", "/menus/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.DELETE, "/place/**", "/menus/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.GET, "/place/**", "/places/**", "/menus/**").hasAnyRole("USER", "OWNER")
                // 리뷰 읽기는 누구나 가능하게 개방
                it.requestMatchers(HttpMethod.GET, "/review/**").permitAll()
                it.requestMatchers(HttpMethod.POST, "/review/**").hasRole("USER")
                it.requestMatchers(HttpMethod.PUT, "/review/**").hasRole("USER")
                it.requestMatchers(HttpMethod.DELETE, "/review/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/tag/**").hasAnyRole("USER", "OWNER")
                it.requestMatchers(HttpMethod.GET, "/gcoo/**").hasAnyRole("USER", "OWNER")
                it.requestMatchers(HttpMethod.POST, "/gcoo/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.PATCH, "/gcoo/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.DELETE, "/gcoo/**").hasRole("OWNER")

                // ROLE_OWNER
                it.requestMatchers(HttpMethod.DELETE, "/admin/coupons/**").hasRole("OWNER")
                it.requestMatchers(HttpMethod.POST, "/admin/coupons/**").hasRole("OWNER")

                // ROLE_USER
                it.requestMatchers(HttpMethod.POST, "/comment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.DELETE, "/comment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/blog/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/blog/**").hasRole("USER")
                it.requestMatchers(HttpMethod.PATCH, "/blog/**").hasRole("USER")
                it.requestMatchers(HttpMethod.DELETE, "/blog/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/blogComment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/blogComment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.PATCH, "/blogComment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.DELETE, "/blogComment/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/blogLike/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/blogLike/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/blogCommentLike/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/blogCommentLike/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/coupon/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/coupon/**").hasRole("USER")
                it.requestMatchers(HttpMethod.DELETE, "/coupon/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/places/**").hasRole("USER")
                it.requestMatchers(HttpMethod.GET, "/comments/**").hasRole("USER")
                it.requestMatchers(HttpMethod.POST, "/comments/**").hasRole("USER")


                it.anyRequest().authenticated()
            }

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
