package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.coupon.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @PostMapping
    public ResponseEntity<ApiResponse<CouponIssueResponse>> issueCoupon(
            @RequestBody CouponIssueRequest req) {
        if (req.couponId() == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "COUPON_SOLD_OUT");
        }
        return ResponseEntity.ok(ApiResponse.success(
                new CouponIssueResponse(
                        10L,
                        "CODE123",
                        1L,
                        String.valueOf(new Date()),
                        String.valueOf(new Date(new Date().getTime() + (long) ( 130 * 60 * 60 * 24 ))))
                )
        );
    }
}