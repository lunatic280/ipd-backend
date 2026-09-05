package com.example.Ipd.coupon.controller

import com.example.Ipd.coupon.domain.CouponPolicy
import com.example.Ipd.coupon.domain.CouponSource
import com.example.Ipd.coupon.domain.CouponStatus
import com.example.Ipd.coupon.dto.CouponPreviewRequest
import com.example.Ipd.coupon.dto.CouponPreviewResponse
import com.example.Ipd.coupon.dto.UserCouponResponse
import com.example.Ipd.coupon.service.CouponPolicyService
import com.example.Ipd.coupon.service.UserCouponService
import com.example.Ipd.user.service.CustomUserDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupon")
class UserCouponController(
    private val couponPolicyService: CouponPolicyService,
    private val userCouponService: UserCouponService
) {

    @GetMapping("/")
    fun getUserCoupon(
        @AuthenticationPrincipal user: CustomUserDetails,
        @RequestParam status: CouponStatus?
    ): List<UserCouponResponse> {
        val userId = user.getId()
        return userCouponService.getUserCoupons(userId, status)
    }

    @PostMapping("/{userCouponId}/preview")
    fun previewDiscount(
        @PathVariable userCouponId: Long,
        @RequestBody request: CouponPreviewRequest,
        @AuthenticationPrincipal user: CustomUserDetails
    ): CouponPreviewResponse {
        return userCouponService.previewDiscount(user.getId(), userCouponId, request.orderPrice)
    }

    @PostMapping("/create/{policyId}/issue")
    fun issueCoupon(
        @AuthenticationPrincipal user: CustomUserDetails,
        @PathVariable("policyId") policyId: Long,
        @RequestParam source: CouponSource
    ): ResponseEntity<UserCouponResponse> {
        val result = userCouponService.issueCoupon(user.getId(), policyId, source)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
}
