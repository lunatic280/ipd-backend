package com.example.Ipd.coupon.repository

import com.example.Ipd.coupon.domain.CouponStatus
import com.example.Ipd.coupon.domain.UserCoupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface UserCouponRepository : JpaRepository<UserCoupon, Long> {

    fun findByUserId(userId: Long): List<UserCoupon>

    fun findByUserIdAndStatus(userId: Long,status: CouponStatus): List<UserCoupon>

    fun findByIdAndUserId(id: Long, userId: Long): UserCoupon?

    @Modifying
    @Transactional
    @Query("delete from UserCoupon c where c.status = 'AVAILABLE' and c.issuedAt <= :cutoff")
    fun deleteExpiredIssuedBefore(cutoff: LocalDateTime): Int
}
