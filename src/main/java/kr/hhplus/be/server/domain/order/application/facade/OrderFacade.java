package kr.hhplus.be.server.domain.order.application.facade;

import kr.hhplus.be.server.config.aop.lock.DistributedLock;
import kr.hhplus.be.server.config.aop.lock.LockType;
import kr.hhplus.be.server.config.aop.lock.Resource;
import kr.hhplus.be.server.config.aop.lock.ResourceKey;
import kr.hhplus.be.server.domain.coupon.application.CouponIssue;
import kr.hhplus.be.server.domain.coupon.application.service.CouponService;
import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.exception.AlreadyProcessedOrderException;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateRequest;
import kr.hhplus.be.server.domain.order.controller.dto.OrderCreateResponse;
import kr.hhplus.be.server.domain.order.application.service.OrderService;
import kr.hhplus.be.server.domain.order.command.mapper.OrderMapper;
import kr.hhplus.be.server.domain.product.application.facade.InventoryFacade;
import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
import kr.hhplus.be.server.domain.product.exception.RestoreOutOfStockException;
import kr.hhplus.be.server.domain.user.application.AlreadyProcessedPointException;
import kr.hhplus.be.server.domain.user.application.Users;
import kr.hhplus.be.server.domain.user.application.service.UserService;
import kr.hhplus.be.server.domain.user.exception.InsufficientBalanceException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final CouponService couponService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final InventoryFacade inventoryFacade;

    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest req) {
        Users user = userService.getUser(req.userId());
        CouponIssue couponIssue = couponService.couponAppliedByOrder(user.getUserId(), req.couponCode());

        Order order = orderMapper.toDomain(req);
        if (couponIssue != null) {
            order.getOrderLines()
                    .forEach(line -> line.applyCoupon(couponIssue.getDiscountRate()));
        }

        // 3) 저장
        Order saved = orderService.createOrder(order);

        // 4) 응답 DTO 변환
        return orderMapper.toResponse(saved);
    }


    private void orderCheck(Order order){
        if(!order.isWaitedOrder()){
            if(order.getStatus().equals("O_CMPL")){
                throw new AlreadyProcessedOrderException(order.getOrderId(), order.getOrderCode());
            }else{
                throw new AlreadyProcessedOrderException(order.getOrderId(), order.getOrderCode());
            }
        }
    }

    public Order getOrderByCode(String orderCode){
        return orderService.getOrderByCode(orderCode);
    }


    @Transactional
    public void orderComplete(Order order){
        try{
            orderCheck(order);
            orderService.orderComplete(order);
        }catch(AlreadyProcessedOrderException ex){
            throw ex;
        }
        catch(ObjectOptimisticLockingFailureException ex){
            throw new AlreadyProcessedOrderException(order.getOrderId(), order.getOrderCode());
        }
    }

    @DistributedLock(
            type = LockType.ORDER,
            keys = {
                    @ResourceKey(resource = Resource.ORDER, key = "#order.orderId"),
                    @ResourceKey(resource = Resource.STOCK, key = "#productLineId"),
                    @ResourceKey(resource = Resource.POINT, key = "#order.userId"),
            }
    )
    @Transactional(
            rollbackFor = {
                    OutOfStockException.class,
                    RestoreOutOfStockException.class,
                    InsufficientBalanceException.class,
                    AlreadyProcessedPointException.class,
                    AlreadyProcessedOrderException.class,
            }
    )
    public Order orderComplete(Order order, List<Long> productLineId){
        try{
            orderCheck(order);

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
            return orderService.orderComplete(order);

        }catch(AlreadyProcessedOrderException ex){
            throw ex;
        }catch(ObjectOptimisticLockingFailureException ex){
            throw new AlreadyProcessedOrderException(order.getOrderId(), order.getOrderCode());
        }catch (Exception ex){
            throw ex;
        }
    }

    /*
    @Retryable(
            retryFor = {org.springframework.orm.ObjectOptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 50) // 50ms 대기 후 재시도
    )
    */
    @Transactional
    public void orderFailed(Order order){
        try{
            orderCheck(order);

            Long userId = order.getUserId();
            String cCode = order.getOrderLines().get(0).getCouponCode();

            if(cCode != null && !cCode.isEmpty() && !cCode.isBlank()){
                CouponIssue couponIssue = couponService.couponRestoreByPayment(order.getUserId(), cCode);
            }

            orderService.orderFailed(order);
        }catch(AlreadyProcessedOrderException ex){
            throw ex;
        }catch(ObjectOptimisticLockingFailureException ex){
            throw new AlreadyProcessedOrderException(order.getOrderId(), order.getOrderCode());
        }
    }


}
