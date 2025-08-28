package kr.hhplus.be.server.domain.order.application.saga;

import kr.hhplus.be.server.common.exception.order.AlreadyProcessedOrderException;
import kr.hhplus.be.server.domain.order.adapter.event.OrderCompletedEvent;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.product.command.ProductRankingDto;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;


public class OrderSaga {

    private final Order order;
    private final List<Long> productLineIds;

    private final OrderService orderService;
    private final UserService userService;
    private final InventoryFacade inventoryFacade;

    private OrderState state;

    public OrderSaga(Order order,
                     List<Long> productLineIds,
                     InventoryFacade invFacade,
                     UserService userService,
                     OrderService orderService) {
        this.order = order;
        this.productLineIds = productLineIds;
        this.inventoryFacade = invFacade;
        this.userService = userService;
        this.orderService = orderService;
        this.state = OrderState.INIT;
    }

    public Order start(){
        try{
            // 주문 상태 확인
            order.isPending();
            transition(OrderState.STARTED);

            // 1. 재고 주문 요청 수량 만큼 감소된 아이템들
            // RestoreOutOfStockException, OutOfStockException
            transition(OrderState.STOCK_DECREASING);
            inventoryFacade.checkStockWithLock(order, productLineIds);

            // 2. 유저 포인트 차감
            // InsufficientBalanceException
            transition(OrderState.POINT_DECREASING);
            Users used = userService.payPointWithLock(
                    order.getUserId(),
                    order.getTotalPrice(),
                    order.getOrderCode()
            );

            // 3. 주문 상태 변경
            // AlreadyProcessedOrderException
            transition(OrderState.PROCESSING);
            return orderService.orderComplete(order);

        }catch (Exception ex){
            compensate(ex);
            throw ex;
        }
    }

    private void compensate(Exception ex){
        OrderState lastState = state;

        // todo : 추후 이벤트 기반 보상 트랜잭션 작동 예정
        transition(OrderState.COMPENSATING);
        switch(lastState){
            case PROCESSING -> {
                // 주문 상태 변경 중  실패
                // → 포인트 환불
                // → 재고 원복
                transition(OrderState.FAILED);
                userService.chargePointWithLock(order.getUserId(), order.getTotalPrice(), order.getOrderCode());
                inventoryFacade.restoreStockWithLock(order, productLineIds);
            }
            case POINT_DECREASING -> {
                // 포인트 차감 중 실패
                // → 포인트 환불
                // → 재고 원복 (@Transactional에 의해 자동 원복)
                transition(OrderState.POINT_COMPENSATE);
                inventoryFacade.restoreStockWithLock(order, productLineIds);
            }
            case STOCK_DECREASING -> {
                // 재고 차감 중 실패
                // → 재고 원복(@Transactional에 의해 자동 원복)
                transition(OrderState.STOCK_COMPENSATE);
            }
        }
        transition(OrderState.COMPENSATED);

        if(lastState == OrderState.INIT){
            // 주문 락을 걸기 때문에,
            // 주문 종료 시 중복 결제 요청 확인 시에는
            // 이미 주문 상태가 바뀌어 있을것,,
        }else{
            orderService.orderFailed(order);
        }
    }

    private void transition(OrderState newState) {
        this.state = newState;
        // todo : 현재 주문에 대한 Redis로 상태머신 전송
    }

    private enum OrderState {
        INIT("결제 최초 요청"),
        STARTED("결제 시작"),
        STOCK_DECREASING("재고 차감 중"),
        STOCK_COMPENSATE("재고 원복 중"),
        POINT_DECREASING("포인트 차감 중"),
        POINT_COMPENSATE("포인트 원복 중"),

        PROCESSING("주문 상태 변경 중"),
        COMPLETED("주문 완료"),

        COMPENSATING("보상 트랜잭션 수행 시작"),
        COMPENSATED("보상 트랜잭션 완료"),

        FAILED("주문 실패");

        private final String description;

        OrderState(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }
    }
}
