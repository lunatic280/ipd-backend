package com.example.Ipd.place.domain

import com.example.Ipd.menu.domain.Menu
import com.example.Ipd.place.dto.PlaceDto
import com.example.Ipd.review.domain.Review
import com.example.Ipd.review.dto.ReviewDto
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import java.time.OffsetDateTime

@Entity
class Place protected constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100, unique = true)
    val title: String,

    @Column(nullable = false, length = 200)
    private var description: String,

    @Column(nullable = false)
    private var  telNumber: String,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    val createdByUserId: Long,

    @OneToMany(
        mappedBy = "place",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val likes: MutableList<PlaceLike> = mutableListOf(),

    @OneToMany(
        mappedBy = "place",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val reviews: MutableList<Review> = mutableListOf(),

    @OneToMany(
        mappedBy = "place",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val menus: MutableList<Menu> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "place_images", joinColumns = [JoinColumn(name = "place_id")])
    @Column(name = "image_url")
    val imageUrls: MutableList<String> = mutableListOf()

    ) {

    fun updateDescription(newDescription: String) {
        description = newDescription
    }

    fun getDescription(): String = description

    fun addImageUrls(urls: List<String>) {
        imageUrls.addAll(urls)
    }

    fun toDto(): PlaceDto {
        return PlaceDto(
            id = this.id,
            title = this.title,
            description = this.description,
            latitude = this.latitude,
            longitude = this.longitude,
            createdAt = this.createdAt.toString(),
            likeCount = this.likes.size,
            telNumber = this.telNumber,
            type = this.type,
            menus = this.menus.mapNotNull { it.id },
            imageUrls = this.imageUrls.toList()
        )
    }
    companion object {
        fun create(
            title: String,
            description: String,
            latitude: Double,
            longitude: Double,
            createdByUserId: Long,
            type: String,
            telNumber: String,
            imageUrls: List<String> = emptyList()
        ): Place {
            return Place(
                title = title,
                description = description,
                latitude = latitude,
                longitude = longitude,
                createdByUserId = createdByUserId,
                type = type,
                telNumber = telNumber,
                imageUrls = imageUrls.toMutableList()
            )
        }
    }
}