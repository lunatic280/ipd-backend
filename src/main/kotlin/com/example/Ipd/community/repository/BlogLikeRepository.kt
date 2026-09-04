package com.example.Ipd.community.repository

import com.example.Ipd.community.domain.BlogLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BlogLikeRepository: JpaRepository<BlogLike, Long> {

    // 유저 + 블로그로 좋아요 엔티티 조회
    @Query(
        """
        select bl
        from BlogLike bl
        where bl.userId = :userId
          and bl.blog.id = :blogId
        """
    )
    fun findByUserIdAndBlogId(
        @Param("userId") userId: Long,
        @Param("blogId") blogId: Long
    ): BlogLike?

    // 유저가 해당 블로그에 이미 좋아요 했는지 여부
    @Query(
        """
        select case when count(bl) > 0 then true else false end
        from BlogLike bl
        where bl.userId = :userId
          and bl.blog.id = :blogId
        """
    )
    fun existByUserIdAndBlogId(
        @Param("userId") userId: Long,
        @Param("blogId") blogId: Long
    ): Boolean

    // 블로그별 좋아요 개수
    @Query(
        """
        select count(bl)
        from BlogLike bl
        where bl.blog.id = :blogId
        """
    )
    fun countByBlogId(
        @Param("blogId") blogId: Long
    ): Long
}
