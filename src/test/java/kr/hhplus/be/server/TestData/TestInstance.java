package kr.hhplus.be.server.TestData;

import kr.hhplus.be.server.domain.order.application.Order;
import kr.hhplus.be.server.domain.order.application.OrderLine;
import kr.hhplus.be.server.domain.product.application.Product;
import kr.hhplus.be.server.domain.product.application.ProductLine;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestInstance {


    public static class MockProduct{
        public final Product product = Product.builder()
                .productId(1L)
                .productName("맥북 14 Pro")
                .productPrice(new BigDecimal("32000"))
                .updateDt(LocalDateTime.now())
                .build();

        public final ProductLine productLine1 = ProductLine.builder()
                .productLineId(1L)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("31000"))
                .productLineType("m2 그레이 실버")
                .updateDt(LocalDateTime.now())
                .build();

        public final ProductLine productLine2 = ProductLine.builder()
                .productLineId(2L)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("33000"))
                .productLineType("m3 스페이스 블랙")
                .updateDt(LocalDateTime.now())
                .build();

        public static MockProduct getMockProduct(){
            return new MockProduct();
        }
    }

    public static class PersistProduct{
        public final Product product = Product.builder()
                .productName("맥북 14 Pro")
                .productPrice(new BigDecimal("32000"))
                .updateDt(LocalDateTime.now())
                .build();

        public final ProductLine productLine1 = ProductLine.builder()
                .productLineId(null)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("31000"))
                .productLineType("m2 그레이 실버")
                .remaining(10L)
                .updateDt(LocalDateTime.now())
                .build();

        public final ProductLine productLine2 = ProductLine.builder()
                .productLineId(null)
                .productId(1L)
                .productName("맥북 14 Pro")
                .productLinePrice(new BigDecimal("33000"))
                .productLineType("m3 스페이스 블랙")
                .remaining(5L)
                .updateDt(LocalDateTime.now())
                .build();

        public static PersistProduct getPersistProduct(){
            return new PersistProduct();
        }
    }


    public static class MockOrder{
        public List<OrderLine> oLineData = IntStream.rangeClosed(1,4)
                .mapToObj(i ->
                        OrderLine.builder()
                                .orderLineId((long) i)
                                .orderId("ORDER_MOCK_ID")
                                .userId(1L)
                                .productId(1L)
                                .productLineId((long)i)
                                .orderLinePrice(BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(i)))
                                .quantity(1)
                                .status("O_MAKE")
                                .orderDt(LocalDateTime.now())
                                .updateDt(LocalDateTime.now())
                                .build()
                ).collect(Collectors.toList());

        public Order orderData = Order.builder()
                .orderId("ORDER_MOCK_ID")
                .userId(1L)
                .totalPrice(BigDecimal.valueOf(100000))
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .orderLines(oLineData)
                .build();

        public static MockOrder getMockOrder(){
            return new MockOrder();
        }
    }


    public static class PersistOrder{
        public List<OrderLine> oLineData = IntStream.rangeClosed(1,4)
                .mapToObj(i ->
                        OrderLine.builder()
                                //.orderLineId((long) i)
                                .orderId("ORDER_MOCK_ID")
                                .userId(1L)
                                .productId(1L)
                                .productLineId((long)i)
                                .orderLinePrice(BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(i)))
                                .quantity(1)
                                .status("O_MAKE")
                                .orderDt(LocalDateTime.now())
                                .updateDt(LocalDateTime.now())
                                .build()
                ).collect(Collectors.toList());

        public Order orderData = Order.builder()
                .orderId("ORDER_MOCK_ID")
                .userId(1L)
                .totalPrice(BigDecimal.valueOf(100000))
                .orderDt(LocalDateTime.now())
                .status("O_MAKE")
                .updateDt(LocalDateTime.now())
                .orderLines(oLineData)
                .build();

        public static PersistOrder getPersistOrder(){
            return new PersistOrder();
        }
    }
}
