import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
    vus: 1,
    duration: '2m',
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
    summaryTrendStats: ['min', 'med', 'avg', 'p(90)', 'p(95)', 'max'],
};

export default function () {
    const res = http.post(
        `${BASE_URL}/api/first-aid/advice`,
        JSON.stringify({
            symptomType: 'BURNS',
            symptomDetail: '뜨거운 물에 손을 데었어요',
            latitude: 37.5665,
            longitude: 126.978,
        }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
        'country identified': (r) =>
            r.json('data.identificationResponse.countryCode') === 'KR',
        'ai response served': (r) => r.json('data.content') !== null,
    });

    sleep(1);
}
