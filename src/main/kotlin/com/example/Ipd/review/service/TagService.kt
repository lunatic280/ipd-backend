package com.example.Ipd.review.service

import com.example.Ipd.review.domain.Tag
import com.example.Ipd.review.dto.TagDto
import com.example.Ipd.review.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository
) {

    fun getAllTag(): List<TagDto> {
        return tagRepository.findAll().map { it.toDto() }
    }
}
