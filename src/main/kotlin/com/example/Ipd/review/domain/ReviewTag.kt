package com.example.Ipd.review.domain

import com.example.Ipd.review.domain.Review
import com.example.Ipd.review.domain.Tag
import jakarta.persistence.*

@Entity
@Table(
    name = "review_tag",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_review_tag_review_id_tag_id",
            columnNames = ["review_id", "tag_id"]
        )
    ]
)
class ReviewTag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    val review: Review,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag
)
