package com.example.Ipd.review.domain

import jakarta.persistence.*

@Entity
class ReviewComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    val review: Review,

    @Column(nullable = false)
    val comment: String
) {
}