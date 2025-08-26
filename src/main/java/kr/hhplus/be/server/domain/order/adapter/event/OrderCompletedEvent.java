package kr.hhplus.be.server.domain.order.adapter.event;

import kr.hhplus.be.server.domain.product.command.ProductRankingDto;

import java.time.LocalDate;
import java.util.List;

public record OrderCompletedEvent(
        LocalDate orderDate,
        List<ProductRankingDto.ProductItemForRank> items
)
{}
