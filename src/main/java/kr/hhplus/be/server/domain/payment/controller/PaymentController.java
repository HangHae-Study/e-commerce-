package kr.hhplus.be.server.domain.payment.controller;

import kr.hhplus.be.server.domain.payment.command.PaymentCreateCommand.*;
import kr.hhplus.be.server.domain.payment.application.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentFacade facade;
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest req) {
        var resp = facade.process(req);
        return ResponseEntity.ok(resp);
    }

}