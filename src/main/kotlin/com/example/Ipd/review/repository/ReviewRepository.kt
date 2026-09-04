package com.example.Ipd.review.repository

import com.example.Ipd.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository: JpaRepository<Review, Long> {
}