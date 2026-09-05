package com.example.Ipd.review.controller

import com.example.Ipd.review.dto.TagDto
import com.example.Ipd.review.service.TagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tag")
class TagController(
    private val tagService: TagService
) {

    @GetMapping()
    fun getAllTag(): ResponseEntity<List<TagDto>> {
        val tagList = tagService.getAllTag()
        return ResponseEntity.ok(tagList)
    }
}
