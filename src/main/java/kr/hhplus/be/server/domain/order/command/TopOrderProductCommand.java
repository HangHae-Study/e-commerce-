package kr.hhplus.be.server.domain.order.command;

import kr.hhplus.be.server.domain.order.adapter.projection.BestSellingProductLineProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

public class TopOrderProductCommand {
    @ToString
    @AllArgsConstructor
    public static class TopOrderProductResponse implements BestSellingProductLineProjection {
        private Long productLineId;
        private Long orderQuantity;

        @Override
        public Long getProductLineId() {
            return productLineId;
        }

        @Override
        public Long getTotalQuantity() {
            return orderQuantity;
        }
    }
}
