package kr.hhplus.be.server.domain.coupon.controller;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueRequest;
import kr.hhplus.be.server.domain.coupon.controller.dto.CouponIssueResponse;
import kr.hhplus.be.server.domain.coupon.controller.mapper.CouponIssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponIssueMapper couponIssueMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listCoupon(){

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CouponIssueResponse>> issueCoupon(
            @RequestBody CouponIssueRequest req) {

        CouponIssue couponIssue = couponService.newCouponIssue(req.userId(), req.couponId());
        CouponIssueResponse resp = couponIssueMapper.toResponse(couponIssue);

        return ResponseEntity.ok(ApiResponse.success(resp));
    }
}