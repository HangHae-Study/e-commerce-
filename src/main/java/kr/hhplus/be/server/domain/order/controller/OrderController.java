package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.common.api.ApiResponse;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.application.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.application.dto.OrderKeyResponse;
import kr.hhplus.be.server.domain.order.application.facade.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @GetMapping("/key")
    public ResponseEntity<ApiResponse<OrderKeyResponse>> issueOrderKey() {
        String orderId = UUID.randomUUID().toString();
        return ResponseEntity.ok(ApiResponse.success(new OrderKeyResponse(orderId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderCreateResponse>> createOrder(
            @RequestBody OrderCreateRequest req) {

        OrderCreateResponse response = orderFacade.createOrder(req);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

