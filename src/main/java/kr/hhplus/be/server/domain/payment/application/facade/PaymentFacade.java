package kr.hhplus.be.server.domain.payment.application.facade;

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
import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
import kr.hhplus.be.server.domain.product.exception.RestoreOutOfStockException;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import kr.hhplus.be.server.domain.user.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderService orderService;
    private final InventoryFacade inventoryFacade;
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentResponse process(PaymentRequest req) {
        Order order = orderService.getOrderByCode(req.orderCode());
        try{
            // 재고 주문 요청 수량 만큼 감소된 아이템들
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
        }catch(RestoreOutOfStockException restEx){
            // 복구 로직 - 5개중 4개만 업데이트 되었다면, 4개만 되돌리기? => checkStock 에서 @Transactional로 해결
            //inventoryFacade.restoreStock(restEx.getSucceededLines(), 0);
            throw new IllegalStateException("재고가 부족하여 결제에 실패하였습니다.");
        }catch (InsufficientBalanceException ex){
            // 복구 로직
            //inventoryFacade.restoreStock(order.getOrderLines(), 0);
            throw new IllegalStateException("잔고가 부족하여 결제에 실패하였습니다.");
        }
    }

}