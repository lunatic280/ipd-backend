package com.example.Ipd.gcoo.repository

import com.example.Ipd.gcoo.domain.Gcoo
import org.springframework.data.jpa.repository.JpaRepository

interface GcooRepository: JpaRepository<Gcoo, Long> {
}