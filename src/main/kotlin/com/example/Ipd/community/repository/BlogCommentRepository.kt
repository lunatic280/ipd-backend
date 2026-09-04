package com.example.Ipd.community.repository

import com.example.Ipd.community.domain.BlogComment
import org.springframework.data.jpa.repository.JpaRepository

interface BlogCommentRepository: JpaRepository<BlogComment, Long> {
}