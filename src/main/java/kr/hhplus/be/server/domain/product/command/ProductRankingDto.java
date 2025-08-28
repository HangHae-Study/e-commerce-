package kr.hhplus.be.server.domain.product.command;

import kr.hhplus.be.server.domain.order.application.Order;

import java.util.List;

public class ProductRankingDto {

    public record ProductItemForRank(
            Long productLineId,
            int quantity
    ){}

    public static List<ProductItemForRank> fromOrder(Order order){
        return order.getOrderLines().stream().map(
                v -> new ProductItemForRank(v.getProductLineId(), v.getQuantity())
        ).toList();
    }

    public record ProductRankEntry(
            Long productLineId,
            int score
    ){}
}
