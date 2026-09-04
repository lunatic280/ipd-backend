package com.example.Ipd.coupon.domain

import com.example.Ipd.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class UserCoupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id", nullable = false)
    val couponPolicy: CouponPolicy,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: CouponStatus = CouponStatus.AVAILABLE,

    @Column(nullable = false)
    val issuedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val expiredAt: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val couponSource: CouponSource
) {
    fun isExpired(now: LocalDateTime = LocalDateTime.now()): Boolean =
        expiredAt.isBefore(now)

    fun use(now: LocalDateTime = LocalDateTime.now()) {
        check(!isExpired(now)) { "Expired coupon cannot be used." }
        check(status == CouponStatus.AVAILABLE) {
            "Coupon cannot be used in status: $status"
        }
        status = CouponStatus.USED
    }

    fun calculateDiscountFor(orderPrice: Int, now: LocalDateTime = LocalDateTime.now()): Int {
        if (isExpired(now)) return 0
        if (status != CouponStatus.AVAILABLE) return 0
        return couponPolicy.calculateDiscount(orderPrice)
    }
}
