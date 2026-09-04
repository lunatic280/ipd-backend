package com.example.Ipd.community.repository

import com.example.Ipd.community.domain.Blog
import org.springframework.data.jpa.repository.JpaRepository

interface BlogRepository: JpaRepository<Blog, Long> {
}