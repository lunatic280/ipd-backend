import com.example.Ipd.coupon.domain.CouponPolicy
import com.example.Ipd.coupon.domain.CouponStatus
import com.example.Ipd.coupon.domain.UserCoupon
import com.example.Ipd.coupon.dto.AdminCreateAndIssueCouponRequest
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
class CouponAdminService(
    private val userRepository: UserRepository,
    private val couponPolicyRepository: CouponPolicyRepository,
    private val userCouponRepository: UserCouponRepository
) {

    /**
     * 관리자 유스케이스:
     * 1) 쿠폰 정책 생성
     * 2) 특정 유저에게 해당 정책 쿠폰 1장 발급
     * 을 하나의 트랜잭션에서 처리
     */
    @Transactional
    fun createPolicyAndIssueToUser(req: AdminCreateAndIssueCouponRequest): UserCouponResponse {
        // 1. 유저 검증
        val user = userRepository.findById(req.userId)
            .orElseThrow {
                BusinessException("USER_NOT_FOUND", "유저가 존재하지 않습니다.", HttpStatus.NOT_FOUND)
            }

        // 2. 쿠폰 정책 생성
        val policy = CouponPolicy(
            name = req.name,
            discountAmount = req.discountAmount,
            discountRate = req.discountRate,
            minOrderPrice = req.minOrderPrice,
            description = req.description
        )
        val savedPolicy = couponPolicyRepository.save(policy)

        // 3. 만료일 계산
        val now = LocalDateTime.now()
        val expiredAt = now.plusDays(req.validDays)

        // 4. 유저 쿠폰 발급
        val userCoupon = UserCoupon(
            couponPolicy = savedPolicy,
            user = user,
            status = CouponStatus.AVAILABLE,
            couponSource = req.source,
            issuedAt = now,
            expiredAt = expiredAt
        )
        val savedUserCoupon = userCouponRepository.save(userCoupon)

        // 5. 응답 DTO로 변환
        return savedUserCoupon.toResponse()
    }

    // 엔티티 -> 응답 DTO 매핑
    private fun UserCoupon.toResponse(): UserCouponResponse =
        UserCouponResponse(
            id = this.id!!,
            name = this.couponPolicy.name,
            discountAmount = this.couponPolicy.discountAmount ?: 0,
            description = this.couponPolicy.description,
            source = this.couponSource,
            status = this.status,
            expiredAt = this.expiredAt
        )
}
