package com.example.Ipd.review.repository

import com.example.Ipd.review.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
}