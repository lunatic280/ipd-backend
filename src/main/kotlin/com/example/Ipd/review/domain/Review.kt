package com.example.Ipd.review.domain


import com.example.Ipd.place.domain.Place
import com.example.Ipd.review.dto.ReviewDto
import com.example.Ipd.user.domain.User
import jakarta.persistence.*

@Entity
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    private var reviewComment: String,

    @Column(nullable = false)
    private var bssComment: String,

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = [JoinColumn(name = "review_id")])
    @Column(name = "image_url")
    val imageUrl: MutableList<String> = mutableListOf(),

    @OneToMany(
        mappedBy = "review",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @Column(name = "review_tag")
    private val reviewTags: MutableList<ReviewTag> = mutableListOf()
    ) {

    fun getBssComment() = bssComment
    fun getReviewComment() = reviewComment
    fun updateReviewComment(newComment: String) {
        reviewComment = newComment
    }
    fun updateBssComment(newComment: String) {
        bssComment = newComment
    }
    fun updateImageUrl(newUrls: List<String>) {
        imageUrl.clear()
        imageUrl.addAll(newUrls)
    }

    fun addTag(tag: Tag) {
        reviewTags.add(
            ReviewTag(
                review = this,
                tag = tag
            )
        )
    }

    fun getTag(): List<Tag> = reviewTags.map { it.tag }

    fun setTag(tags: Collection<Tag>) {
        reviewTags.clear()
        tags.forEach { addTag(it) }
    }



    fun toDto(): ReviewDto {
        return ReviewDto(
            id = this.id,
            bssComment = this.bssComment,
            reviewComment = this.reviewComment,
            placeId = this.place.id!!,
            userId = this.user.id,
            tagIds = this.getTag().mapNotNull { it.id },
            imageUrl = this.imageUrl
        )
    }
}
