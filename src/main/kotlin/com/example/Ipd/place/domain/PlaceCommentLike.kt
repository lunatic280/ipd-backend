package com.example.Ipd.place.domain

import jakarta.persistence.*

@Entity
@Table(
    name = "place_comment_like",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_place_comment_like_user_comment",
            columnNames = ["user_id", "comment_id"]
        )
    ]
)
class PlaceCommentLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    val comment: PlaceComment
) {
    companion object {
        fun create(userId: Long, comment: PlaceComment): PlaceCommentLike {
            return PlaceCommentLike(
                userId = userId,
                comment = comment
            )
        }
    }
}