package com.example.Ipd.community.domain

import com.example.Ipd.community.dto.BlogCommentDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class BlogComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    private var content: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    val blog: Blog,

    @Column(nullable = false)
    val createdByUserId: Long,

    private var star: Int
) {
    fun updateContent(newContent: String) {
        content = newContent
    }
    fun getContent(): String = content
    fun increaseStar(): Int = ++star
    fun decreaseStar(): Int {
        require(star > 0) { "좋아요는 음수가 될 수 없다" }
        return --star
    }
    fun getStar(): Int = star

    fun toDto(): BlogCommentDto {
        return BlogCommentDto(
            id = this.id,
            content = this.content,
            blogId = this.blog.id!!,
            star = this.star
        )
    }
    companion object {
        fun create(
            content: String,
            blog: Blog,
            star: Int,
            createdByUserId: Long
        ): BlogComment {
            return BlogComment(
                content = content,
                blog = blog,
                star = star,
                createdByUserId = createdByUserId
            )
        }
    }
}