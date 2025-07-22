package kr.hhplus.be.server.domain.order.application;

import java.math.BigDecimal;

public class OrderLine {
    private final Long id;
    private final Long productLineId;
    private final int quantity;
    private final BigDecimal price;

    public OrderLine(Long id, Long productLineId, int quantity, BigDecimal price) {
        this.id = id;
        this.productLineId = productLineId;
        this.quantity = quantity;
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(new BigDecimal(quantity));
    }

    public Long getId() { return id; }
    public Long getProductLineId() { return productLineId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
}

