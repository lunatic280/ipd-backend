package com.example.Ipd.coupon.service

import com.example.Ipd.coupon.repository.UserCouponRepository

import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Service
class CouponExpirationService(
    private val userCouponRepository: UserCouponRepository,
    private val taskScheduler: TaskScheduler
) {

    fun scheduleDeletion(couponId: Long, delayMinutes: Long = 5) {
        val deleteTime = Instant.now().plus(Duration.ofMinutes(delayMinutes))

        taskScheduler.schedule({
            try {
                deleteCoupon(couponId)
            } catch (e: Exception) {
            }
        }, deleteTime)

    }

    @Transactional
    fun deleteCoupon(couponId: Long) {
        if (!userCouponRepository.existsById(couponId)) {
            return
        }

        userCouponRepository.deleteById(couponId)
    }
}
