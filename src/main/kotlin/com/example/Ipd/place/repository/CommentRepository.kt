package com.example.Ipd.place.repository

import com.example.Ipd.place.domain.PlaceComment
import com.example.Ipd.place.domain.Place
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<PlaceComment, Long> {
    fun findByPlace(place: Place)
    fun findAllByPlace(place: Place): List<PlaceComment>
}