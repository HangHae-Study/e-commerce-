package kr.hhplus.be.server.domain.coupon.controller;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponIssueResponse>> issueCoupon(
            @RequestBody CouponIssueRequest req) {

        return ResponseEntity.ok(ApiResponse.success(couponService.couponIssueRes(req)));
    }
}