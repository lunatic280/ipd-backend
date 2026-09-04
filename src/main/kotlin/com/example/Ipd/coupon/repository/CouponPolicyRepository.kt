package com.example.Ipd.coupon.repository

import com.example.Ipd.coupon.domain.CouponPolicy
import org.springframework.data.jpa.repository.JpaRepository

interface CouponPolicyRepository: JpaRepository<CouponPolicy, Long> {
}