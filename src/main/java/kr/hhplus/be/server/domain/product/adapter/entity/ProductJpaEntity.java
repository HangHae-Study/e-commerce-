package kr.hhplus.be.server.domain.product.adapter.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.application.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ProductJpaEntity.java
@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 100, nullable = false)
    private String productName;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal productPrice;

    @UpdateTimestamp
    @Column(
            name = "update_dt",
            nullable = false,
            columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
    )
    private LocalDateTime updateDt;

    public Product toDomain(){
        return Product.
                builder()
                    .productId(productId)
                    .productName(productName)
                    .productPrice(productPrice)
                    .updateDt(updateDt)
                .build();
    }

    public static ProductJpaEntity fromDomain(Product product){
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.productName = product.getProductName();
        entity.productPrice = product.getProductPrice();
        entity.updateDt = product.getUpdateDt();

        if(product.getProductId() != null){
            entity.productId = product.getProductId();
        }

        return entity;
    }
}
