package com.example.Ipd.coupon.dto

data class CouponPolicyCreateRequest(
    val name: String,
    val discountAmount: Int?,
    val discountRate: Int?,
    val minOrderPrice: Int = 0,
    val description: String = ""
)
