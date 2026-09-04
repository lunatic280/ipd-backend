package com.example.Ipd.coupon.dto

import com.example.Ipd.coupon.domain.CouponSource
import com.example.Ipd.coupon.domain.CouponStatus
import java.time.LocalDateTime

data class UserCouponResponse(
    val id: Long,
    val name: String,            // Coupon name
    val discountAmount: Int,     // Maximum discount amount this coupon can apply
    val description: String,
    val source: CouponSource,
    val status: CouponStatus,
    val expiredAt: LocalDateTime
)
