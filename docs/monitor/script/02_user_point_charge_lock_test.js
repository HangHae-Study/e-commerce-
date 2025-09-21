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

export function userScenario() {
    const userId = 1;

    // 1. 포인트 충전 요청 및 조회
    check(chargePoint(userId, 10), { '포인트 충전 성공': (r) => r.status === 200 });
    check(getPoint(userId), { '포인트 조회 성공': (r) => r.status === 200 });

    sleep(1);
}