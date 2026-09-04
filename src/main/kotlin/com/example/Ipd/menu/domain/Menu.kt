package com.example.Ipd.menu.domain

import com.example.Ipd.menu.dto.MenuDto
import com.example.Ipd.place.domain.Place
import jakarta.persistence.*

@Entity
class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    private var name: String,

    @Column(nullable = false)
    private var price: Int,

    @Column(nullable = false)
    private var description: String,

    @Column(nullable = false)
    private var imageUrl: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place
) {

    fun toDto() = MenuDto(
        id = id,
        name = name,
        price = price,
        description = description,
        imageUrl = imageUrl,
        placeId = place.id!!
    )

    // Getter
    fun getName(): String = name
    fun getPrice(): Int = price
    fun getDescription(): String = description
    fun getImageUrl(): String = imageUrl

    // Setter
    fun setName(name: String) {
        this.name = name
    }

    fun setPrice(price: Int) {
        this.price = price
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }
}