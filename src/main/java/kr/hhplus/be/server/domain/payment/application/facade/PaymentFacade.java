package kr.hhplus.be.server.domain.payment.application.facade;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.facade.OrderFacade;
import kr.hhplus.be.server.domain.payment.command.PaymentCreateCommand;
import kr.hhplus.be.server.domain.payment.command.PaymentCreateCommand.PaymentRequest;
import kr.hhplus.be.server.domain.payment.command.PaymentCreateCommand.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final OrderFacade orderFacade;
    private final InventoryFacade inventoryFacade;
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentResponse process(PaymentRequest req) {
        try {
            Order order = orderFacade.getOrderByCode(req.orderCode());
            List<Long> plIdList = order.getOrderLines().stream().map(
                    OrderLine::getProductLineId
            ).toList();

            order = orderFacade.orderComplete(order, plIdList);

            // 4. 결제 정보 생성
            return PaymentCreateCommand.response(paymentService.pay(
                    order.getUserId(),
                    order.getOrderId(),
                    order.getTotalPrice()
            ));

        } catch(Exception ex){
            throw ex;
        }
    }

    /*
    @Transactional(
            rollbackFor = {
                    OutOfStockException.class,
                    RestoreOutOfStockException.class,
                    InsufficientBalanceException.class,
                    AlreadyProcessedPointException.class,
                    AlreadyProcessedOrderException.class,
            }
    )
    public PaymentResponse process(PaymentRequest req) {
        try {
            Order order = orderFacade.getOrderByCode(req.orderCode());

            // 1. 재고 주문 요청 수량 만큼 감소된 아이템들
            // RestoreOutOfStockException, OutOfStockException
            inventoryFacade.checkStock(order);

            // 2, 유저 포인트 차감
            // InsufficientBalanceException
            Users used = userService.payPointWithLock(
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getOrderCode()
            );

            // 3. 주문 상태 변경
            // AlreadyProcessedOrderException
            orderFacade.orderComplete(order);

            // 4. 결제 정보 생성
            return PaymentCreateCommand.response(paymentService.pay(
                    order.getUserId(),
                    order.getOrderId(),
                    order.getTotalPrice()
            ));

        } catch(Exception ex){
            System.out.println("앙");
            throw ex;
        }
    }
     */

}