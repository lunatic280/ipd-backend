package com.example.Ipd.place.repository

import com.example.Ipd.place.domain.PlaceCommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PlaceCommentLikeRepository : JpaRepository<PlaceCommentLike, Long> {

    @Query(
        """
        select pcl
        from PlaceCommentLike pcl
        where pcl.userId = :userId
          and pcl.comment.id = :commentId
        """
    )
    fun findByUserIdAndCommentId(
        @Param("userId") userId: Long,
        @Param("commentId") commentId: Long
    ): PlaceCommentLike?

    @Query(
        """
        select case when count(pcl) > 0 then true else false end
        from PlaceCommentLike pcl
        where pcl.userId = :userId
          and pcl.comment.id = :commentId
        """
    )
    fun existByUserIdAndCommentId(
        @Param("userId") userId: Long,
        @Param("commentId") commentId: Long
    ): Boolean

    @Query(
        """
        select count(pcl)
        from PlaceCommentLike pcl
        where pcl.comment.id = :commentId
        """
    )
    fun countByCommentId(
        @Param("commentId") commentId: Long
    ): Long
}
