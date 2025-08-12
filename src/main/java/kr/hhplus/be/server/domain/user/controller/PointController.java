package kr.hhplus.be.server.domain.user.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.dto.PointDao;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import kr.hhplus.be.server.domain.user.controller.dto.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.controller.dto.BalanceChargeResponse;
import kr.hhplus.be.server.domain.user.controller.dto.BalanceInquiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceInquiryResponse>> getBalance(@PathVariable Long userId) {

        PointDao point = userService.getPoint(userId);

        BalanceInquiryResponse data = new BalanceInquiryResponse(userId, point.getBalance());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceChargeResponse>> charge(
            @PathVariable Long userId,
            @Valid @RequestBody BalanceChargeRequest req) {

        Users user = userService.chargePoint(userId, req.amount());

        BalanceChargeResponse data = new BalanceChargeResponse(user.getUserId(), user.getBalance());

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

