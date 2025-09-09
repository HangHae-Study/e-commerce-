import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
    vus: 10, // 동시에 10명 가상유저(Virtual User)
    duration: "10s", // 10초 동안 실행
};

export default function () {
    let res = http.get("localhost:8080/orders/key", {
        headers: { Connection: "close" },
    });

    // 응답 확인 (단위 테스트처럼)
    check(res, {
        "status is 200": (r) => r.status === 200,
        "body has orderCode": (r) =>       r && r.body && r.body.includes("orderCode"),
    });

    //console.log("Response:", res.status, res.body);

    sleep(1); // 1초 대기 후 다음 요청
}
