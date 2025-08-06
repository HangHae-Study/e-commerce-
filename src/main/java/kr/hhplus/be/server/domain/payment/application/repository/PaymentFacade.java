package kr.hhplus.be.server.domain.payment.application.repository;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.order.application.repository.OrderLineRepository;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.payment.application.Payment;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentRequest;
import kr.hhplus.be.server.domain.payment.application.dto.PaymentResponse;
import kr.hhplus.be.server.domain.payment.application.service.PaymentService;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.user.entity.Point;
import kr.hhplus.be.server.domain.user.facade.UserPointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserPointFacade pointFacade;
    private final OrderService orderService;
    private final ProductLineService productLineService;
    private final PaymentService paymentService;

    @Transactional
    public PaymentResponse process(PaymentRequest req) {
        // 주문 라인 조회
        Order order = orderService.getOrder(req.orderId());
        var lines = order.getOrderLines();

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 주문 번호 입니다: " + req.orderId());
        }

        // 재고 확인
        Map<Long, ProductLine> stockCountMap = new HashMap<>();
        for(OrderLine line: lines){
            Long lId = line.getProductLineId();
            stockCountMap.put(lId, productLineService.getProductLine(lId));
        }
        for (OrderLine line : lines) {
            ProductLine stock = stockCountMap.get(line.getProductLineId());
            if (stock.getRemaining() < line.getQuantity()) {
                throw new IllegalStateException("재고 소진: "+ line.getProductId());
            }
        }

        // 잔고 확인
        Point point = pointFacade.getPoint(order.getUserId());
        if(point.getBalance().compareTo(order.getTotalPrice()) < 0){
            throw new IllegalStateException("잔고 부족");
        }

        try{
            // 재고 감소
            for(OrderLine line : lines){
                ProductLine stock = stockCountMap.get(line.getProductLineId());
                stock.decreaseStock((long) line.getQuantity());
                productLineService.decrease(stock);
            }

            point.use(order.getTotalPrice());
            pointFacade.updateUserPoint(order.getUserId(), order.getTotalPrice());
            orderService.orderComplete(order);

            // 결제 시도
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
            throw ex;
        }
    }

}
