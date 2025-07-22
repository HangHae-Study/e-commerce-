package kr.hhplus.be.server;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import kr.hhplus.be.server.domain.coupon.CouponController;
import kr.hhplus.be.server.domain.order.controller.OrderController;
import kr.hhplus.be.server.domain.payment.PaymentController;
import kr.hhplus.be.server.domain.product.controller.ProductController;
import kr.hhplus.be.server.domain.user.controller.PointController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;



@WebMvcTest({
        PointController.class,
        ProductController.class,
        OrderController.class,
        PaymentController.class,
        CouponController.class
})
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MockAPIDocumentTests {

    @Autowired
    private MockMvc mockMvc;

    // ─── 잔액 조회 / 충전 API ─────────────────────────────────────
    @Nested @DisplayName("잔액 조회 API GET /points/{userId}")
    class BalanceInquiry {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(get("/points/{userId}", 1))
                    .andExpect(status().isOk())
                    .andDo(document("balance-inquiry-200",
                            ResourceSnippetParameters.builder()
                                .description("잔액 조회 API")
                                .responseSchema(Schema.schema("BalanceInquiryResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("userId").description("유저 식별자")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.userId").description("유저 ID"),
                                    fieldWithPath("data.balance").description("현재 잔액")
                            )
                    ));
        }

        @Test @DisplayName("404 Not Found")
        void notFound() throws Exception {
            mockMvc.perform(get("/points/{userId}", 999))
                    .andExpect(status().isNotFound())
                    .andDo(document("balance-inquiry-404",
                            ResourceSnippetParameters.builder()
                                    .description("존재하지 않는 UserId 충전 요청")
                                    .responseSchema(Schema.schema("BalanceInquiryResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (USER_NOT_FOUND)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    @Nested @DisplayName("잔액 충전 API PATCH /points/{userId}")
    class BalanceCharge {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(patch("/points/{userId}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                              { "amount": 5000 }
                            """))
                    .andExpect(status().isOk())
                    .andDo(document("balance-charge-200",
                            ResourceSnippetParameters.builder()
                                    .description("잔액 충전 API")
                                    .requestSchema(Schema.schema("BalanceChargeRequest"))
                                    .responseSchema(Schema.schema("BalanceChargeResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("userId").description("유저 식별자")
                            ),
                            requestFields(
                                    fieldWithPath("amount").description("충전할 금액")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.userId").description("유저 ID"),
                                    fieldWithPath("data.newBalance").description("충전 후 잔액")
                            )
                    ));
        }

        @Test @DisplayName("400 Bad Request")
        void badRequest() throws Exception {
            mockMvc.perform(patch("/points/{userId}", 1)
                            .contentType(MediaType.APPLICATION_JSON).content("""
                              { "amount": -100 }
                            """))
                    .andExpect(status().isBadRequest())
                    .andDo(document("balance-charge-400",
                            ResourceSnippetParameters.builder()
                                    .description("잘못된 충전량 요청")
                                    .requestSchema(Schema.schema("BalanceChargeRequest"))
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("amount").description("잔액 희망 충전량")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (INVALID_REQUEST)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }

        @Test @DisplayName("404 Not Found")
        void notFound() throws Exception {
            mockMvc.perform(patch("/points/{userId}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"amount\": 100 }"))
                    .andExpect(status().isNotFound())
                    .andDo(document("balance-charge-404",
                            ResourceSnippetParameters.builder()
                                    .description("존재하지 않는 UserId 충전 요청")
                                    .requestSchema(Schema.schema("BalanceChargeRequest"))
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("amount").description("잔액 희망 충전량")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (USER_NOT_FOUND)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    // ─── 상품 조회 / 상세 조회 API ─────────────────────────────────
    @Nested @DisplayName("상품 조회 API GET /products")
    class ProductList {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(get("/products"))
                    .andExpect(status().isOk())
                    .andDo(document("product-list-200",
                            ResourceSnippetParameters.builder()
                                    .description("상품 조회 API")
                                    .responseSchema(Schema.schema("ProductListResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.products[].productId").description("상품 ID"),
                                    fieldWithPath("data.products[].name").description("상품명"),
                                    fieldWithPath("data.products[].price").description("상품 가격")
                            )
                    ));
        }
    }

    @Nested @DisplayName("상품 상세 조회 API GET /products/{productId}")
    class ProductDetail {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(get("/products/{id}", 1))
                    .andExpect(status().isOk())
                    .andDo(document("product-detail-200",
                            ResourceSnippetParameters.builder()
                                    .description("상품 상세 조회 API")
                                    .responseSchema(Schema.schema("ProductDetailResponse")),
                            preprocessRequest(prettyPrint()),
                            pathParameters(
                                    parameterWithName("id").description("상품 식별자")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.productId").description("상품 ID"),
                                    fieldWithPath("data.name").description("상품명"),
                                    fieldWithPath("data.description").description("상품 설명"),
                                    fieldWithPath("data.price").description("가격"),
                                    fieldWithPath("data.lines[].productLineId").description("옵션 ID"),
                                    fieldWithPath("data.lines[].lineType").description("옵션 유형"),
                                    fieldWithPath("data.lines[].linePrice").description("옵션 가격"),
                                    fieldWithPath("data.lines[].remaining").description("남은 수량")
                            )
                    ));
        }

        @Test @DisplayName("404 Not Found")
        void notFound() throws Exception {
            mockMvc.perform(get("/products/{id}", 9999))
                    .andExpect(status().isNotFound())
                    .andDo(document("product-detail-404",
                            ResourceSnippetParameters.builder()
                                    .description("존재하지 않는 상품 ID 조회 요청")
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (PRODUCT_NOT_FOUND)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    // ─── 주문 식별자 발행 / 주문 생성 API ────────────────────────────
    @Nested @DisplayName("주문 식별자 발행 API GET /orders/key")
    class OrderKey {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(get("/orders/key"))
                    .andExpect(status().isOk())
                    .andDo(document("order-key-200",
                            ResourceSnippetParameters.builder()
                                    .description("주문 번호 생성 요청 API")
                                    .responseSchema(Schema.schema("OrderKeyResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.orderId").description("발행된 주문 식별자")
                            )
                    ));
        }
    }

    @Nested @DisplayName("주문 생성 API POST /orders")
    class OrderCreate {
        @Test @DisplayName("200 OK (쿠폰 미사용)")
        void successNoCoupon() throws Exception {
            mockMvc.perform(post("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                        {
                          "orderId":"UUID123",
                          "items":[{ "productLineId":10, "linePrice": 200, "quantity":2 }],
                          "couponCode":null
                        }
                        """))
                    .andExpect(status().isOk())
                    .andDo(document("order-create-200",
                            ResourceSnippetParameters.builder()
                                    .description("주문 생성 요청 API")
                                    .requestSchema(Schema.schema("OrderCreateRequest"))
                                    .responseSchema(Schema.schema("OrderCreateResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("orderId").description("주문 식별자"),
                                    fieldWithPath("items[].productLineId").description("상품 라인 ID"),
                                    fieldWithPath("items[].linePrice").description("주문 시점 상품 라인 단일 금액"),
                                    fieldWithPath("items[].quantity").description("수량"),
                                    fieldWithPath("couponCode").description("쿠폰 코드 (없으면 null)")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.orderId").description("주문 ID"),
                                    fieldWithPath("data.items[].productLineId").description("상품 라인 ID"),
                                    fieldWithPath("data.items[].linePrice").description("주문 시점 상품 라인 단일 금액"),
                                    fieldWithPath("data.items[].couponYN").description("쿠폰 사용 여부"),
                                    fieldWithPath("data.items[].discountPrice").description("쿠폰 적용된 할인 금액, 쿠폰 미사용 시 null"),
                                    fieldWithPath("data.items[].quantity").description("수량"),
                                    fieldWithPath("data.items[].orderStatus").description("주문 상태('Y')"),
                                    fieldWithPath("data.totalPrice").description("총 주문 금액"),
                                    fieldWithPath("data.orderDt").description("주문 일시"),
                                    fieldWithPath("data.orderStatus").description("주문 상태")
                            )
                    ));
        }

        @Test @DisplayName("409 Conflict (유효하지 않은 주문 식별자)")
        void conflict() throws Exception {
            mockMvc.perform(post("/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                        {
                          "orderId":"BADID",
                          "items":[{ "productLineId":10, "linePrice": 200, "quantity":2 }],
                          "couponCode":null
                        }
                        """))
                    .andExpect(status().isConflict())
                    .andDo(document("order-create-409",
                            ResourceSnippetParameters.builder()
                                    .description("존재하지 않는 OrderId 주문 요청")
                                    .requestSchema(Schema.schema("OrderCreateRequest"))
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (INVALID_ORDER_ID)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    // ─── 결제 API POST /payments ────────────────────────────────────
    @Nested @DisplayName("결제 API POST /payments")
    class Payment {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            { "orderId":"UUID123" }
                            """))
                    .andExpect(status().isOk())
                    .andDo(document("payment-200",
                            ResourceSnippetParameters.builder()
                                    .description("결제 요청 API")
                                    .requestSchema(Schema.schema("PaymentRequest"))
                                    .responseSchema(Schema.schema("PaymentResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("orderId").description("주문 식별자")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.paymentId").description("결제 ID"),
                                    fieldWithPath("data.orderId").description("주문 ID"),
                                    fieldWithPath("data.totalPrice").description("결제 금액"),
                                    fieldWithPath("data.paymentDt").description("결제 일시"),
                                    fieldWithPath("data.paymentStatus").description("결제 상태")
                            )
                    ));
        }

        @Test @DisplayName("400 Bad Request (포인트 부족)")
        void badRequest() throws Exception {
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"orderId\":\"LOWFUNDS\" }"))
                    .andExpect(status().isBadRequest())
                    .andDo(document("payment-400",
                            ResourceSnippetParameters.builder()
                                    .description("잔액 부족으로 인한 결제 실패")
                                    .requestSchema(Schema.schema("PaymentRequest"))
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (INSUFFICIENT_FUNDS)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }

        @Test @DisplayName("409 Conflict (재고 부족)")
        void conflict() throws Exception {
            mockMvc.perform(post("/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"orderId\":\"OUTOFSTOCK\" }"))
                    .andExpect(status().isConflict())
                    .andDo(document("payment-409",
                            ResourceSnippetParameters.builder()
                                    .description("재고 부족으로 인한 결제 실패")
                                    .requestSchema(Schema.schema("PaymentRequest"))
                                    .responseSchema(Schema.schema("ErrorResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (OUT_OF_STOCK)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    // ─── 선착순 쿠폰 발급 API POST /coupons ──────────────────────────
    @Nested @DisplayName("쿠폰 발급 API POST /coupons")
    class CouponIssue {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(post("/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                        { "userId":1, "couponId":1 }
                        """))
                    .andExpect(status().isOk())
                    .andDo(document("coupon-issue-200",
                            ResourceSnippetParameters.builder()
                                    .description("쿠폰 발급 API")
                                    .requestSchema(Schema.schema("CouponIssueRequest"))
                                    .responseSchema(Schema.schema("CouponIssueResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("userId").description("유저 ID"),
                                    fieldWithPath("couponId").description("쿠폰 ID")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.couponIssueId").description("쿠폰 발급 식별자"),
                                    fieldWithPath("data.couponCode").description("발급된 쿠폰 코드"),
                                    fieldWithPath("data.couponId").description("발행 대상 쿠폰 식별자"),
                                    fieldWithPath("data.issueDt").description("발급 일자"),
                                    fieldWithPath("data.expireDt").description("만료 일자")
                            )
                    ));
        }

        @Test @DisplayName("409 Conflict (쿠폰 소진)")
        void conflict() throws Exception {
            mockMvc.perform(post("/coupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"userId\":1, \"couponId\":0 }"))
                    .andExpect(status().isConflict())
                    .andDo(document("coupon-issue-409",
                            ResourceSnippetParameters.builder()
                                    .description("쿠폰 소진으로 인한 쿠폰 발행 실패")
                                    .requestSchema(Schema.schema("CouponIssueRequest"))
                                    .responseSchema(Schema.schema("ErrorResposne")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("userId").description("유저 ID"),
                                    fieldWithPath("couponId").description("쿠폰 ID")
                            ),
                            responseFields(
                                    fieldWithPath("code").description("에러 코드 (COUPON_SOLD_OUT)"),
                                    fieldWithPath("message").description("에러 메시지"),
                                    fieldWithPath("data").description("응답 데이터 (항상 null)")
                            )
                    ));
        }
    }

    // ─── 인기 판매 상품 조회 API GET /products/top ─────────────────────
    @Nested @DisplayName("인기 판매 상품 조회 API GET /products/top")
    class TopProducts {
        @Test @DisplayName("200 OK")
        void success() throws Exception {
            mockMvc.perform(get("/products/top")
                            .param("limit", "5"))
                    .andExpect(status().isOk())
                    .andDo(document("top-products-200",
                            ResourceSnippetParameters.builder()
                                    .description("인기 판매 상품 조회 API")
                                    .responseSchema(Schema.schema("TopProductRanking")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("code").description("응답 코드"),
                                    fieldWithPath("message").description("응답 메시지"),
                                    fieldWithPath("data.topProducts[].productId").description("상품 ID"),
                                    fieldWithPath("data.topProducts[].productName").description("상품 명"),
                                    fieldWithPath("data.topProducts[].productPrice").description("상품 대표 금액"),
                                    fieldWithPath("data.topProducts[].soldCount").description("판매 수량")
                            )
                    ));
        }
    }

}
