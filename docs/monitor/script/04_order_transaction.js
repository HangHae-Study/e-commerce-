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
                { duration: '30s', target: 30 }, // 10
                { duration: '1m', target: 10 },  // 10
                { duration: '30s', target: 0 },  // 0
            ],
        }
    }
};

const BASE_URL = 'http://host.docker.internal:8081';

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


function topProducts(limit = 5) {
    return http.get(`${BASE_URL}/products/top?limit=${limit}`, {
        tags: { name: 'GET /products/top' }
    });
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


export function userScenario(){
    const userId = randomIntBetween(1,50);

    let topRes = topProducts();
    check(topRes, { '인기 상품 조회 성공': (r) => r.status === 200 });
    const topProductDetail = topRes.json().data;
    const topRandomLine = topProductDetail.topProducts[randomIntBetween(0, topProductDetail.topProducts.length - 1)];
    const topProductLineId = topRandomLine.productId;
    const topProductLinePrice = topRandomLine.productPrice;

    let { res: orderRes2, orderCode: orderCode2 } = createOrder(userId, 1, 2, topProductLinePrice);
    check(orderRes2, { '인기 상품 주문 생성 성공': (r) => r.status === 200 });
    check(pay(orderCode2), { '인기 상품 결제 요청 성공': (r) => r.status === 200 });

    sleep(1);
}