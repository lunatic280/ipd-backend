package com.example.Ipd.review.domain



import com.example.Ipd.review.dto.TagDto
import jakarta.persistence.*
import org.hibernate.engine.internal.Cascade

@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 20)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val tagType: TagType,

    @OneToMany(
        mappedBy = "tag",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    private val reviewTags: MutableList<ReviewTag> = mutableListOf()
) {

    fun toDto(): TagDto {
        return TagDto(
            id = id ?: throw IllegalArgumentException("Tag id cannot be null"),
            name = name,
            tagType = tagType
        )
    }
}