package com.example.Ipd.menu.repository

import com.example.Ipd.menu.domain.Menu
import org.springframework.data.jpa.repository.JpaRepository

interface MenuRepository: JpaRepository<Menu, Long> {
}