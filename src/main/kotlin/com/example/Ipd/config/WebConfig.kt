package com.example.Ipd.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Value("\${app.upload.menu-dir}") private val menuUploadDir: String,
    @Value("\${app.upload.place-dir}") private val placeUploadDir: String,
    @Value("\${app.upload.review-dir}") private val reviewUploadDir: String,
    @Value("\${app.upload.gcoo_dir}") private val gcooUploadDir: String,
    @Value("\${app.cors.allowed-origins:http://localhost:3000}") private val allowedOrigins: String
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins.split(',').map(String::trim).toTypedArray())
            .allowedMethods("GET","POST","PUT","DELETE","PATCH")
            .allowCredentials(true)
    }

    // 전시용 파일 URL 매핑입니다. 업로드 파일을 운영에서 그대로 공개하지 마세요.
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/images/menu/**")
            .addResourceLocations("file:$menuUploadDir/")
        registry.addResourceHandler("/uploads/place/**")
            .addResourceLocations("file:$placeUploadDir/")
        registry.addResourceHandler("/uploads/review/**")
            .addResourceLocations("file:$reviewUploadDir/")
        registry.addResourceHandler("/uploads/gcoo/**")
            .addResourceLocations("file:$gcooUploadDir/")
    }
}
