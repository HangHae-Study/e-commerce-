package kr.hhplus.be.server.domain.user;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.user.dto.BalanceChargeRequest;
import kr.hhplus.be.server.domain.user.dto.BalanceChargeResponse;
import kr.hhplus.be.server.domain.user.dto.BalanceInquiryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/points")
public class BalanceController {

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceInquiryResponse>> getBalance(@PathVariable Long userId) {
        if(userId == 999){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }

        BalanceInquiryResponse data = new BalanceInquiryResponse(userId, 12345);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceChargeResponse>> charge(
            @PathVariable Long userId,
            @Valid @RequestBody BalanceChargeRequest req) {

        if(userId == 999){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }

        BalanceChargeResponse data = new BalanceChargeResponse(userId, 12345 + req.amount());
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}

