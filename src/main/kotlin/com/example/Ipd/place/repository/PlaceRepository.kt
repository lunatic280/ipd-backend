package com.example.Ipd.place.repository

import com.example.Ipd.place.domain.Place
import com.example.Ipd.place.dto.PlaceDto
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRepository: JpaRepository<Place, Long> {
    fun findByTitle(title: String): Place?
}