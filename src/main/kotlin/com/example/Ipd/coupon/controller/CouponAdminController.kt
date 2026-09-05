import com.example.Ipd.coupon.dto.AdminCreateAndIssueCouponRequest
import com.example.Ipd.coupon.dto.UserCouponResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// com.example.coupon.controller.CouponAdminController.kt
@RestController
@RequestMapping("/admin/coupons")
class CouponAdminController(
    private val couponAdminService: CouponAdminService
) {

    /**
     * 관리자: 쿠폰 정책 생성 + 특정 유저에게 1장 발급
     *
     * POST /api/admin/coupons/create-and-issue
     */
    @PostMapping("/create-and-issue")
    @PreAuthorize("hasRole('OWNER')")
    fun createPolicyAndIssueToUser(
        @RequestBody req: AdminCreateAndIssueCouponRequest
    ): ResponseEntity<UserCouponResponse> {
        val result = couponAdminService.createPolicyAndIssueToUser(req)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
}
