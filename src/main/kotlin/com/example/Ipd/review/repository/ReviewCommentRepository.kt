package com.example.Ipd.review.repository

import com.example.Ipd.review.domain.ReviewComment
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewCommentRepository : JpaRepository<ReviewComment, Long> {
}