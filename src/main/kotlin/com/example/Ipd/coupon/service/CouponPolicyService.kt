package com.example.Ipd.coupon.service

import com.example.Ipd.coupon.domain.CouponPolicy
import com.example.Ipd.coupon.dto.CouponPolicyCreateRequest
import com.example.Ipd.coupon.dto.CouponPolicyResponse
import com.example.Ipd.coupon.repository.CouponPolicyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CouponPolicyService(
    private val couponPolicyRepository: CouponPolicyRepository
) {

    @Transactional
    fun createCouponPolicy(request: CouponPolicyCreateRequest): CouponPolicyResponse {
        val policy = CouponPolicy(
            name = request.name,
            discountAmount = request.discountAmount,
            discountRate = request.discountRate,
            minOrderPrice = request.minOrderPrice,
            description = request.description
        )
        val saved = couponPolicyRepository.save(policy)
        return saved.toResponse()
    }

    fun getCouponPolicy(): List<CouponPolicyResponse> =
        couponPolicyRepository.findAll().map { it.toResponse() }

    private fun CouponPolicy.toResponse() = CouponPolicyResponse(
        id = id!!,
        name = name,
        discountAmount = discountAmount,
        discountRate = discountRate,
        minOrderPrice = minOrderPrice,
        description = description
    )
}
