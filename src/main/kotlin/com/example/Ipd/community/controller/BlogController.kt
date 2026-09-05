package com.example.Ipd.community.controller

import com.example.Ipd.community.dto.BlogDto
import com.example.Ipd.community.service.BlogService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/blog")
class BlogController(
    private val blogService: BlogService
) {

    @GetMapping("/")
    fun getBlogAll(): ResponseEntity<List<BlogDto>> {
        val findAllBlog = blogService.getAllBlog()
        return runCatching {
            ResponseEntity.ok().body(findAllBlog)
        }.getOrElse {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{id}")
    fun getBlogById(@PathVariable("id") blogId: Long): ResponseEntity<BlogDto> {
        val findBlogById = blogService.getBlogById(blogId)
        return runCatching {
            ResponseEntity.ok().body(findBlogById)
        }.getOrElse {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/create")
    fun createBlog(
        @RequestBody blogDto: BlogDto,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<BlogDto> {
        val savedBlog = blogService.createBlog(
            blogDto,
            loginUser.getId()
        )
        val uri = URI.create("/${savedBlog.id}")
        return runCatching {
            ResponseEntity.created(uri).body(savedBlog)
        }.getOrElse {
            exception -> Unit
            ResponseEntity.badRequest().build()
        }
    }

    @PatchMapping("/update/{id}")
    fun updateBlog(
        @PathVariable("id") blogId: Long,
        @RequestBody newBlogDto: BlogDto,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<BlogDto> {
        val updateBlog = blogService.updateBlog(newBlogDto, blogId, loginUser.getId())
        runCatching {
            return ResponseEntity.ok().body(updateBlog)
        }.getOrElse {
            exception -> Unit
            return ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/delete/{id}")
    fun deleteBlog(
        @PathVariable("id") blogId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<out Any> {
        val deleteBlog = blogService.deleteBlog(blogId, loginUser.getId())
        return ResponseEntity.ok().build()
    }

}
