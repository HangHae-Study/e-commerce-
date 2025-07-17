package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.payment.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(
            @RequestBody PaymentRequest req) {
        switch (req.orderId()) {
            case "LOWFUNDS":
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_FUNDS");
            case "OUTOFSTOCK":
                throw new ResponseStatusException(HttpStatus.CONFLICT, "OUT_OF_STOCK");
            default:
                return ResponseEntity.ok(ApiResponse.success(new PaymentResponse(
                        1L,
                        "UUID123",
                        700.0,
                        String.valueOf(new Date()),
                        "P_CMPL")));
        }
    }
}