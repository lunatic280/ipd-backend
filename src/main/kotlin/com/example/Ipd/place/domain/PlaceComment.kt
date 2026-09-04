package com.example.Ipd.place.domain

import com.example.Ipd.place.dto.CommentDto
import jakarta.persistence.*

@Entity
class PlaceComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place,

    @Column(nullable = false, length = 1000)
    private var content: String,

    @Column(nullable = false)
    val createByUserId: Long,

    @OneToMany(
        mappedBy = "comment",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val likes: MutableList<PlaceCommentLike> = mutableListOf()

) {

    fun toDto(): CommentDto {
        return CommentDto(
            id = this.id,
            placeId = this.place.id!!,
            content = this.content
        )
    }

    fun updateContent(newContent: String) {
        content = newContent
    }

    fun getContent(): String = content

    companion object {
        fun create(place: Place, content: String, createByUserId: Long): PlaceComment {
            return PlaceComment(
                place = place,
                content = content,
                createByUserId = createByUserId
            )
        }
    }
}