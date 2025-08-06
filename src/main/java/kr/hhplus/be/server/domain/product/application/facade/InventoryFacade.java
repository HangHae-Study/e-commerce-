package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryFacade {
    private final ProductLineService productLineService;

    /** 주문 건 전체 재고가 충분한지 확인 (부족하면 예외) */
    public void checkStock(Order order) {
        order.getOrderLines().forEach(line -> {
            ProductLine pl = productLineService.getProductLine(line.getProductLineId());
            pl.decreaseStock((long) line.getQuantity());  // domain 메서드 안에서 부족하면 OutOfStockeException
            productLineService.updateProductLine(pl);
        });
    }

    /** 체크 후 실패했을 때 롤백용 원복 */
    public void restoreStock(Order order) {
        order.getOrderLines().forEach(line -> {
            ProductLine pl = productLineService.getProductLine(line.getProductLineId());
            pl.increaseStock((long) line.getQuantity());
            productLineService.updateProductLine(pl);
        });
    }
}

