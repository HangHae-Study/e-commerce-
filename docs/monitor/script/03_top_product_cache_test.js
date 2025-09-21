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
                { duration: '30s', target: 50 }, // 10
                { duration: '1m', target: 30 },  // 10
                { duration: '30s', target: 0 },  // 0
            ],
        }
    }
};

const BASE_URL = 'http://host.docker.internal:8081';


function topProducts(limit = 5) {
    return http.get(`${BASE_URL}/products/top?limit=${limit}`, {
        tags: { name: 'GET /products/top' }
    });
}

export function userScenario() {
    const userId = 1;

    // 1. 포인트 충전 요청 및 조회
    let topRes = topProducts();
    check(topRes, { '인기 상품 조회 성공': (r) => r.status === 200 });

    sleep(1);
}