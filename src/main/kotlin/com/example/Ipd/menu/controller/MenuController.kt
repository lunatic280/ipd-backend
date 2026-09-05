package com.example.Ipd.menu.controller

import com.example.Ipd.menu.dto.MenuDto
import com.example.Ipd.menu.service.MenuService

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/menus")
class MenuController(
    private val menuService: MenuService
) {

    @PreAuthorize("hasAnyRole('OWNER')")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createMenu(
        @RequestPart("request") dto: MenuDto,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<MenuDto> {
        val savedMenu = menuService.createMenu(dto, image)
        return ResponseEntity.ok().body(savedMenu)
    }

    @GetMapping
    fun getMenus(): ResponseEntity<List<MenuDto>> {
        val menuList =  menuService.getMenus()
        return ResponseEntity.ok().body(menuList)
    }

    @GetMapping("/{id}")
    fun getByMenuId(
        @PathVariable("id") menuId: Long
    ): ResponseEntity<MenuDto> {
        val findMenu = menuService.getByMenuId(menuId)
        return ResponseEntity.ok().body(findMenu)
    }

    @PreAuthorize("hasAnyRole('OWNER')")
    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateMenu(
        @PathVariable("id") menuId: Long,
        @RequestPart("request") dto: MenuDto,
        @RequestPart("image", required = false) image: MultipartFile?
    ): ResponseEntity<MenuDto> {
        val updatedMenu = menuService.updateMenu(menuId, dto, image)
        return ResponseEntity.ok().body(updatedMenu)
    }
}
