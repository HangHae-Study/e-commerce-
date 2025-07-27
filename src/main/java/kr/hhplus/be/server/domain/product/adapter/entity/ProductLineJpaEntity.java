package kr.hhplus.be.server.domain.product.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.application.ProductLine;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// ProductLineJpaEntity.java
@Entity
@Table(name = "product_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLineJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productLineId;

    @Column(nullable = false)
    private Long productId;

    @Column(length = 100, nullable = false)
    private String productName;

    @Column(precision = 12, scale = 2)
    private BigDecimal productLinePrice;

    @Column(nullable = false)
    private String productLineType;

    private Long remaining;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public ProductLine toDomain() {
        return ProductLine.builder()
                .productLineId(productLineId)
                .productId(productId)
                .productName(productName)
                .productLinePrice(productLinePrice)
                .productLineType(productLineType)
                .remaining(remaining)
                .updateDt(updateDt)
                .build();
    }

    public static ProductLineJpaEntity fromDomain(ProductLine line) {
        ProductLineJpaEntity entity = new ProductLineJpaEntity();
        entity.productId = line.getProductId();
        entity.productName = line.getProductName();
        entity.productLinePrice = line.getProductLinePrice();
        entity.productLineType = line.getProductLineType();
        entity.remaining = line.getRemaining();
        entity.updateDt = line.getUpdateDt();

        if(line.getProductLineId() != null){
            entity.productLineId = line.getProductLineId();
        }

        return entity;
    }
}

