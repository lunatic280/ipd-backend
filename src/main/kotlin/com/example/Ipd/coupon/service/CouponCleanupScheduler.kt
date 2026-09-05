package com.example.Ipd.coupon.service

import com.example.Ipd.coupon.repository.UserCouponRepository

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class CouponCleanupScheduler(
    private val userCouponRepository: UserCouponRepository
) {

    /**
     * 매 1분마다 생성된 지 5분 이상 지난 사용 가능 쿠폰을 정리한다.
     */
    @Transactional
    @Scheduled(fixedDelayString = "\${coupon.cleanup.interval-ms:60000}")
    fun deleteOldCoupons() {
        val cutoff = LocalDateTime.now().minusMinutes(5)
        val deleted = userCouponRepository.deleteExpiredIssuedBefore(cutoff)
    }
}
