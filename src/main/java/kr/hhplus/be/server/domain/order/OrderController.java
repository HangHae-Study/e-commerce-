package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.order.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/key")
    public ResponseEntity<ApiResponse<OrderKeyResponse>> issueOrderKey() {
        String orderId = UUID.randomUUID().toString();
        return ResponseEntity.ok(ApiResponse.success(new OrderKeyResponse(orderId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @RequestBody OrderCreateRequest req) {
        if (req.orderId().startsWith("BAD")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "INVALID_ORDER_ID");
        }

        List<OrderCreateResponse.OrderResItem> items = List.of(
                new OrderCreateResponse.OrderResItem(
                        11L, 200.0, "N", null,2, "O_CMPL")
                ,
                new OrderCreateResponse.OrderResItem(
                        12L, 300.0, "N", null,1, "O_CMPL")

        );

        OrderCreateResponse response = new OrderCreateResponse(
                req.orderId(),
                items,
                700.0,
                String.valueOf(new Date()),
                "O_CMPL"
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

