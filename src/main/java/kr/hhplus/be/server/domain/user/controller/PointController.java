package kr.hhplus.be.server.domain.user.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.user.dto.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.dto.BalanceChargeResponse;
import kr.hhplus.be.server.domain.user.dto.BalanceInquiryResponse;
import kr.hhplus.be.server.domain.user.entity.Point;
import kr.hhplus.be.server.domain.user.facade.UserPointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final UserPointFacade userPointFacade;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceInquiryResponse>> getBalance(@PathVariable Long userId) {

        Point point = userPointFacade.getPoint(userId);

        BalanceInquiryResponse data = new BalanceInquiryResponse(point.getUserId(), point.getBalance());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceChargeResponse>> charge(
            @PathVariable Long userId,
            @Valid @RequestBody BalanceChargeRequest req) {

        Point point = userPointFacade.chargeUserPoint(userId, req.amount());

        BalanceChargeResponse data = new BalanceChargeResponse(point.getUserId(), point.getBalance());

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

