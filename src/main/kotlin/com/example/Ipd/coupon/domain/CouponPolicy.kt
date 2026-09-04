package com.example.Ipd.coupon.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class CouponPolicy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = true)
    val discountAmount: Int? = null,    // Fixed discount amount

    @Column(nullable = true)
    val discountRate: Int? = null,      // Percentage discount (%)

    @Column(nullable = false)
    val minOrderPrice: Int = 0,

    @Column(nullable = false)
    val description: String = "",

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    init {
        require(discountAmount != null || discountRate != null) {
            "Either discountAmount or discountRate must be provided."
        }
    }

    fun calculateDiscount(orderPrice: Int): Int {
        require(orderPrice >= 0) { "orderPrice must be greater than or equal to 0." }

        if (orderPrice < minOrderPrice) return 0

        val discount: Int = discountAmount
            ?: discountRate?.let { rate -> orderPrice * rate / 100 }
            ?: 0

        return discount.coerceAtLeast(0)
    }


}
