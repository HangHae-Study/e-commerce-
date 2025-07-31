package kr.hhplus.be.server.domain.payment.application.repository;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderService orderService;
    private final InventoryFacade inventoryFacade;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public PaymentResponse process(PaymentRequest req) {
        Order order = orderService.getOrderByCode(req.orderCode());

        try{
            inventoryFacade.checkStock(order);

            Users used = userService.usePoint(order.getUserId(), order.getTotalPrice());

            orderService.orderComplete(order);

            Payment paid = paymentService.pay(
                    order.getUserId(),
                    order.getOrderId(),
                    order.getTotalPrice()
            );

            return new PaymentResponse(
                    paid.getPaymentId(),
                    paid.getOrderId(),
                    paid.getTotalPrice(),
                    paid.getPaymentDt().toString(),
                    paid.getStatus()
            );
        }catch (Exception ex){
            // 복구 로직
            inventoryFacade.restoreStock(order);
            throw ex;
        }// catch(발생 예외별 복구 로직 추가 작성 필요)
    }

}
