package com.example.Ipd.community.domain

import com.example.Ipd.community.dto.BlogDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Blog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    private var title: String,

    @Column(nullable = false)
    private var content: String,

    @Column(nullable = false)
    val createdByUserId: Long,

    private var star: Int = 0

) {
    fun getTitle(): String = title
    fun updateTitle(newTitle: String) {
        title = newTitle
    }
    fun getContent(): String = content
    fun updateContent(newContent: String) {
        content = newContent
    }
    fun increaseStar(): Int = star++
    fun decreaseStar(): Int {
        require(star > 0) { "좋아요는 음수가 될 수 없다" }
        return star
    }
    fun toDto(): BlogDto {
        return BlogDto(
            id = this.id,
            title = this.title,
            content = this.content,
            star = this.star,
        )
    }

    companion object {
        fun create(
            title: String,
            content: String,
            star: Int,
            createdUserId: Long
        ): Blog {
            return Blog(
                title = title,
                content = content,
                star = star,
                createdByUserId = createdUserId
            )
        }
    }
}