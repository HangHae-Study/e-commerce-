package kr.hhplus.be.server.domain.product;


import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductTestInstance {

    /** ID 없이, 라인 없이 단순 상품 생성 */
    public static Product simpleProduct() {
        return Product.builder()
                .productName("테스트상품")
                .productPrice(new BigDecimal("1000"))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** ID 없이, 하나의 기본 라인을 포함한 상품 생성 */
    public static Product productWithOneLine() {
        ProductLine line = simpleLine();
        return Product.builder()
                .productName("테스트상품2")
                .productPrice(new BigDecimal("2000"))
                .productLines(List.of(line))
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** ID 없이, 기본 값으로 세팅된 ProductLine 객체 */
    public static ProductLine simpleLine() {
        return ProductLine.builder()
                .productLineName("라인A")
                .productLinePrice(new BigDecimal("1100"))
                .productLineType("STD")
                .remaining(10L)
                .updateDt(LocalDateTime.now())
                .build();
    }

    /** ID 포함, DB 저장 후 검증용으로 꺼내 쓸 때 사용 */
    public static Product persistedProduct(Long id) {
        return Product.builder()
                .productId(id)
                .productName("테스트상품")
                .productPrice(new BigDecimal("1000"))
                .updateDt(LocalDateTime.now())
                .build();
    }

    public static ProductLine persistedLine(Long lineId, Long productId) {
        return ProductLine.builder()
                .productLineId(lineId)
                .productId(productId)
                .productLineName("라인A")
                .productLinePrice(new BigDecimal("1100"))
                .productLineType("STD")
                .remaining(10L)
                .updateDt(LocalDateTime.now())
                .build();
    }
}
