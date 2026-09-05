package com.example.Ipd.place.controller

import com.example.Ipd.place.dto.CommentDto
import com.example.Ipd.place.service.CommentService
import com.example.Ipd.user.service.CustomUserDetails

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/comment")
class CommentController(
    private val commentService: CommentService
) {

    @PostMapping("/add")
    fun addComment(
        @RequestBody commentDto: CommentDto,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<CommentDto> {
        val savedComment = commentService.addComment(commentDto, loginUser.getId())
        val uri = URI.create("/comment/${savedComment.id}")

        return ResponseEntity.created(uri).body(savedComment)
    }

    @GetMapping("/{placeId}")
    fun getComment(@PathVariable("placeId") placeId: Long): ResponseEntity<List<CommentDto>> {
        val commentList: List<CommentDto> = commentService.getAllComment(placeId)
        return ResponseEntity.ok(commentList)
    }

    @DeleteMapping("/delete/{id}")
    fun deleteComment(
        @PathVariable("id") commentId: Long,
        @AuthenticationPrincipal loginUser: CustomUserDetails
    ): ResponseEntity<Unit> {
        commentService.deleteComment(commentId, loginUser.getId())
        return ResponseEntity.noContent().build()
    }
}
