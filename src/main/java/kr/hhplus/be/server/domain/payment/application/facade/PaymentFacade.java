package kr.hhplus.be.server.domain.payment.application.facade;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.exception.AlreadyProcessedOrderException;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
import kr.hhplus.be.server.domain.product.exception.RestoreOutOfStockException;
import kr.hhplus.be.server.domain.user.application.AlreadyProcessedPointException;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import kr.hhplus.be.server.domain.user.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderService orderService;
    private final InventoryFacade inventoryFacade;
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentResponse process(PaymentRequest req) {
        try {
            return doProcess(req);
        } catch (Exception ex) {
            // 보상 로직: 실패 시 재고/포인트 복구, 주문 상태 변경
            rollback(req.orderCode(), ex);
            throw ex;
        }
    }

    @Transactional
    public PaymentResponse doProcess(PaymentRequest req) {

        Order order = orderService.getOrderByCode(req.orderCode());

        // 재고 주문 요청 수량 만큼 감소된 아이템들
        inventoryFacade.checkStock(order); // RestoreOutOfStockException

        Users used = userService.payPointWithLock(order.getUserId(), order.getTotalPrice(), order.getOrderCode()); // InsufficientBalanceException

        orderService.orderComplete(order); // AlreadyProcessedOrderException

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
    }

    @Transactional
    protected void rollback(String orderCode, Exception cause) {
        // 1) 주문 락으로 다시 조회
        Order order = orderService.getOrderByCode(orderCode);
        if(order.getStatus().equals("O_MAKE")){
            // 2) 주문 상태를 FAILED로
            orderService.orderFailed(order);
        }

        if(cause instanceof RestoreOutOfStockException){
            // todo: 주문 실패
        }else if(cause instanceof InsufficientBalanceException || // 잔고 부족
                cause instanceof AlreadyProcessedPointException || // 처리된 포인트
                cause instanceof AlreadyProcessedOrderException // 처리된 주문
                //cause instanceof ObjectOptimisticLockingFailureException // 처리된 주문 2
        ){
            // 3) 재고 복구
            inventoryFacade.restoreStock(order);
        }


    }

}