package com.example.Ipd.coupon.service

import com.example.Ipd.coupon.domain.CouponSource
import com.example.Ipd.coupon.domain.CouponStatus
import com.example.Ipd.coupon.domain.UserCoupon
import com.example.Ipd.coupon.dto.CouponPreviewResponse
import com.example.Ipd.coupon.dto.UserCouponResponse
import com.example.Ipd.coupon.repository.CouponPolicyRepository
import com.example.Ipd.coupon.repository.UserCouponRepository
import com.example.Ipd.error.exception.BusinessException
import com.example.Ipd.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class UserCouponService(
    private val userRepository: UserRepository,
    private val userCouponRepository: UserCouponRepository,
    private val couponPolicyRepository: CouponPolicyRepository
) {

    @Transactional
    fun issueCoupon(userId: Long, policyId: Long, source: CouponSource): UserCouponResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException("USER_NOT_FOUND", "User not found.", HttpStatus.NOT_FOUND) }

        val policy = couponPolicyRepository.findById(policyId)
            .orElseThrow { BusinessException("POLICY_NOT_FOUND", "Coupon policy not found.", HttpStatus.NOT_FOUND) }

        val expiredAt = LocalDateTime.now().plusMinutes(5) // expires in 5 minutes

        val coupon = UserCoupon(
            couponPolicy = policy,
            user = user,
            status = CouponStatus.AVAILABLE,
            expiredAt = expiredAt,
            couponSource = source
        )

        val saved = userCouponRepository.save(coupon)

        //5遺꾨뮘 ??젣 ?덉빟

        return UserCouponResponse(
            id = saved.id!!,
            name = policy.name,
            discountAmount = policy.discountAmount ?: 0,
            description = policy.description,
            source = saved.couponSource,
            status = saved.status,
            expiredAt = saved.expiredAt
        )
    }

    fun getUserCoupons(userId: Long, status: CouponStatus?): List<UserCouponResponse> {
        val coupons = if (status != null) {
            userCouponRepository.findByUserIdAndStatus(userId, status)
        } else {
            userCouponRepository.findByUserId(userId)
        }
        return coupons.map { it.toResponse() }
    }

    fun previewDiscount(userId: Long, userCouponId: Long, orderPrice: Int): CouponPreviewResponse {
        val coupon = userCouponRepository.findByIdAndUserId(userCouponId, userId)
            ?: throw BusinessException("COUPON_NOT_FOUND", "荑좏룿 ?놁쓬", HttpStatus.NOT_FOUND)

        if (coupon.isExpired()) {
            throw BusinessException("EXPIRED", "留뚮즺??荑좏룿", HttpStatus.BAD_REQUEST)
        }

        if (coupon.status != CouponStatus.AVAILABLE) {
            throw BusinessException("UNAVAILABLE", "?ъ슜 遺덇??ν븳 荑좏룿", HttpStatus.BAD_REQUEST)
        }

        val discount = coupon.couponPolicy.calculateDiscount(orderPrice)
        val finalPrice = (orderPrice - discount).coerceAtLeast(0)

        return CouponPreviewResponse(
            orderPrice = orderPrice,
            discountAmount = discount,
            finalPrice = finalPrice
        )
    }

    private fun UserCoupon.toResponse() = UserCouponResponse(
        id = id!!,
        name = couponPolicy.name,
        discountAmount = couponPolicy.discountAmount ?: 0,
        description = couponPolicy.description,
        source = couponSource,
        status = status,
        expiredAt = expiredAt
    )



}
