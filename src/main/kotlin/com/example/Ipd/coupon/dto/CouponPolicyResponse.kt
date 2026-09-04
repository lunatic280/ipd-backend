package com.example.Ipd.coupon.dto

data class CouponPolicyResponse(
    val id: Long,
    val name: String,
    val discountAmount: Int?,
    val discountRate: Int?,
    val minOrderPrice: Int,
    val description: String
)
