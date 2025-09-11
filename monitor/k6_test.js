import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween, uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    scenarios: {
        create_order_scenario: {
            exec: 'userScenario',
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 10 },
                { duration: '1m', target: 10 },
                { duration: '30s', target: 0 },
            ],
        }
    }
};

const BASE_URL = 'http://host.docker.internal:8081';

// ========== API 함수 ==========

function chargePoint(userId, amount) {
    return http.patch(
        `${BASE_URL}/points/${userId}`,
        JSON.stringify({
            amount: amount,
            reqId: `REQ-${userId}-${uuidv4()}`
        }),
        {
            headers: { 'Content-Type': 'application/json' },
            tags: { name: 'PATCH /points/{userId}' }   // 👈 태그 추가
        }
    );
}

function getPoint(userId) {
    return http.get(`${BASE_URL}/points/${userId}`, {
        tags: { name: 'GET /points/{userId}' }
    });
}

function listProducts() {
    return http.get(`${BASE_URL}/products`, {
        tags: { name: 'GET /products' }
    });
}

// 상품 상세 조회
function getProduct(productId) {
    return http.get(`${BASE_URL}/products/${productId}`, {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'GET /products/{productId}' }
    });
}

function createOrder(userId, productLineId, quantity, productPrice) {
    const orderCode = `ORD-${userId}-${uuidv4()}`;
    const totalPrice = productPrice * quantity;

    const res = http.post(`${BASE_URL}/orders`,
        JSON.stringify({
            orderCode: orderCode,
            userId: userId,
            totalPrice: 10000,
            items: [
                {
                    productLineId: productLineId,
                    linePrice: productPrice,
                    quantity: quantity
                }
            ],
            couponCode: null
        }),
        {
            headers: { 'Content-Type': 'application/json' },
            tags: { name: 'POST /orders' }
        }
    );

    return { res, orderCode };
}


function pay(orderCode) {
    return http.post(`${BASE_URL}/payments`,
        JSON.stringify({ orderCode: orderCode }),
        {
            headers: { 'Content-Type': 'application/json' },
            tags: { name: 'POST /payments' }
        }
    );
}

function topProducts(limit = 5) {
    return http.get(`${BASE_URL}/products/top?limit=${limit}`, {
        tags: { name: 'GET /products/top' }
    });
}

// ========== 시나리오 본문 ==========

/*
1. 유저 포인트 충전
2. 유저 포인트 조회
3. 유저 상품 전체 조회
4. 유저 주문 생성
5. 유저 결제 요청
6. 유저 인기 상품 조회
7. 유저 포인트 충전
8. 유저 인기상품 주문 생성
9. 유저 주문 생성
10. 유저 결제 요청
11. 유저 포인트 조회
 */
export function userScenario() {
    const userId = randomIntBetween(1, 50);

    // 1. 포인트 충전 요청 및 조회
    check(chargePoint(userId, 999999), { '포인트 충전 성공': (r) => r.status === 200 });
    check(getPoint(userId), { '포인트 조회 성공': (r) => r.status === 200 });

    // 2. 상품 목록 조회
    let productsRes = listProducts();
    check(productsRes, { '상품 목록 조회 성공': (r) => r.status === 200 });
    const products = productsRes.json().data.products;
    const randomProduct = products[randomIntBetween(0, products.length - 1)];
    // const productLineId = randomProduct.productLineId; 상품목록에는 productLineId가 없음..
    const productId = randomProduct.productId;

    // 3-1. 유저 상품 상세 조회
    let detailRes = getProduct(productId);
    check(detailRes, { '상품 상세 조회 성공': (r) => r.status === 200 });
    const productDetail = detailRes.json().data;
    const randomLine = productDetail.lines[randomIntBetween(0, productDetail.lines.length - 1)];
    const productLineId = randomLine.productLineId;
    const productPrice = randomLine.linePrice;

// 4. 유저 주문 생성
    let { res: orderRes1, orderCode: orderCode1 } = createOrder(userId, productLineId, 1.0, productPrice);
    check(orderRes1, { '주문 생성 성공': (r) => r.status === 200 });
    check(pay(orderCode1), { '첫번째 결제 요청 성공': (r) => r.status === 200 });

    /*
    let topRes = topProducts();
    check(topRes, { '인기 상품 조회 성공': (r) => r.status === 200 });
    const topProductDetail = topRes.json().data;
    const topRandomLine = productDetail.topProducts[randomIntBetween(0, topProductDetail.topProducts.length - 1)];
    const topProductLineId = topRandomLine.productId;
    const topProductLinePrice = topRandomLine.productPrice;

    check(chargePoint(userId, 100000), { '포인트 재충전 성공': (r) => r.status === 200 });
    let { res: orderRes2, orderCode: orderCode2 } = createOrder(userId, topProductLineId, 2, topProductLinePrice);
    check(orderRes2, { '인기상품 주문 생성 성공': (r) => r.status === 200 });
    check(pay(orderCode2), { '추가 결제 요청 성공': (r) => r.status === 200 });

    check(getPoint(userId), { '최종 포인트 조회 성공': (r) => r.status === 200 });
    */
    sleep(1);
}
