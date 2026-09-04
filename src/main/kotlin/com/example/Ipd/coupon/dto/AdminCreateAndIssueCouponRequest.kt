package com.example.Ipd.coupon.dto

import com.example.Ipd.coupon.domain.CouponSource


data class AdminCreateAndIssueCouponRequest(
    val userId: Long,             // 쿠폰 받을 유저 ID

    val name: String,             // 쿠폰 이름 (정책 이름)
    val discountAmount: Int?,     // 정액 할인 (원). 정률이면 null
    val discountRate: Int?,       // 정률 할인 (%). 정액이면 null
    val minOrderPrice: Int = 0,   // 최소 주문 금액
    val description: String = "", // 설명

    val source: CouponSource = CouponSource.GCOO,  // 발급 출처
    val validDays: Long = 30                       // 유효기간 (며칠짜리 쿠폰인지)
)
