package com.example.Ipd.place.repository

import com.example.Ipd.place.domain.PlaceLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PlaceLikeRepository : JpaRepository<PlaceLike, Long> {

    @Query(
        """
        select pl
        from PlaceLike pl
        where pl.userId = :userId
          and pl.place.id = :placeId
        """
    )
    fun findByUserIdAndPlaceId(
        @Param("userId") userId: Long,
        @Param("placeId") placeId: Long
    ): PlaceLike?

    @Query(
        """
        select case when count(pl) > 0 then true else false end
        from PlaceLike pl
        where pl.userId = :userId
          and pl.place.id = :placeId
        """
    )
    fun existByUserIdAndPlaceId(
        @Param("userId") userId: Long,
        @Param("placeId") placeId: Long
    ): Boolean

    @Query(
        """
        select count(pl)
        from PlaceLike pl
        where pl.place.id = :placeId
        """
    )
    fun countByPlaceId(
        @Param("placeId") placeId: Long
    ): Long
}
