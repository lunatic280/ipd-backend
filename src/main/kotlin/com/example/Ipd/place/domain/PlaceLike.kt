package com.example.Ipd.place.domain

import jakarta.persistence.*

@Entity
@Table(
    name = "place_like",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_place_like_user_place",
            columnNames = ["user_id", "place_id"]
        )
    ]
)
class PlaceLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place
) {
}