package com.example.Ipd.community.repository

import com.example.Ipd.community.domain.BlogCommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BlogCommentLikeRepository: JpaRepository<BlogCommentLike, Long> {


    @Query(
        """
        select count(bcl)
        from BlogCommentLike bcl
        where bcl.blogComment.id = :commentId
        """
    )
    fun countByCommentId(
        @Param("commentId") commentId: Long
    ): Long

    @Query(
        """
        select case when count(bcl) > 0 then true else false end
        from BlogCommentLike bcl
        where bcl.blogComment.id = :commentId
          and bcl.userId = :userId
        """
    )
    fun existsByCommentIdAndUserId(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long
    ): Boolean

    @Query(
        """
        select bcl
        from BlogCommentLike bcl
        where bcl.blogComment.id = :commentId
          and bcl.userId = :userId
        """
    )
    fun findByCommentIdAndUserId(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long
    ): BlogCommentLike?
}