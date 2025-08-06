package kr.hhplus.be.server.domain.product.application.facade;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import kr.hhplus.be.server.domain.product.application.service.ProductLineService;
import kr.hhplus.be.server.domain.product.exception.OutOfStockException;
import kr.hhplus.be.server.domain.product.exception.RestoreOutOfStockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryFacade {
    private final ProductLineService productLineService;


    /** 주문 건 전체 재고가 충분한지 확인 (부족하면 예외) */
    @Transactional
    public void checkStock(Order order) {
        List<OrderLine> succeededStockLines = new ArrayList<>();
        try{
            order.getOrderLines().forEach(line -> {
                ProductLine pl = productLineService.getProductLine(line.getProductLineId());

                pl.decreaseStock((long) line.getQuantity());  // domain 메서드 안에서 부족하면 OutOfStockeException
                productLineService.updateProductLine(pl);

                succeededStockLines.add(line); // 복구 로직 책임
            });
        }catch(OutOfStockException outEx) {
            // 복구 로직 - 5개중 4개만 업데이트 되었다면, 4개만 되돌리기?
            throw new RestoreOutOfStockException("재고 감소에 실패하였습니다.", succeededStockLines);
        }
    }

    public void restoreStock(List<OrderLine> lines, int var) {
        lines.forEach(line -> {
            ProductLine pl = productLineService.getProductLine(line.getProductLineId());
            pl.increaseStock((long) line.getQuantity());
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

