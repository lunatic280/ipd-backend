package com.example.Ipd.gcoo.domain

import com.example.Ipd.gcoo.dto.GcooDto
import com.example.Ipd.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.springframework.data.geo.Distance
import java.net.URI

@Entity
class Gcoo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    private var charge: Int,

    @Column(nullable = false)
    private var distance: Int,

    @Column(nullable = false)
    private var subTitle: String,

    @Column(nullable = false)
    private var description: String,

    @Column(nullable = false)
    private var imageUrl: String,

    @Column(nullable = false)
    private var latitude: Double,

    @Column(nullable = false)
    private var longitude: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
) {

    fun setCharge(newCharge: Int) {
        charge = newCharge
    }

    fun setDistance(newDistance: Int) {
        distance = newDistance
    }

    fun setSubTitle(newSubTitle: String) {
        subTitle = newSubTitle
    }

    fun setDescription(newDescription: String) {
        description = newDescription
    }

    fun setImageUrl(newImageUrl: String) {
        imageUrl = newImageUrl
    }

    fun setLatitude(newLatitude: Double) {
        latitude = newLatitude
    }

    fun setLongitude(newLongitude: Double) {
        longitude = newLongitude
    }

    fun toDto() : GcooDto{
        return GcooDto(
            id = id,
            charge = charge,
            distance = distance,
            subTitle = subTitle,
            description = description,
            imageUrl = imageUrl,
            latitude = latitude,
            longitude = longitude,
            userId = user.id
        )
    }
}
