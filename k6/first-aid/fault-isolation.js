import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const MGMT_URL = __ENV.MGMT_URL || 'http://localhost:9090';
const STUB_ADMIN_URL = __ENV.STUB_ADMIN_URL || 'http://localhost:8090/__admin';

const SYMPTOM_TYPES = ['BLEEDING', 'BURNS', 'FRACTURE', 'ALLERGIC_REACTION', 'SEIZURE'];
const JSON_HEADERS = { headers: { 'Content-Type': 'application/json' } };

const NORMAL_DELAY_MS = 4000;
const DEGRADED_DELAY_MS = 60000;

const DEGRADE_START_SEC = 120;
const DEGRADE_END_SEC = 300;
const TEST_DURATION_SEC = 600;

const GEMINI_STUB_TEXT = [
    'CONTENT:',
    'Move away from the heat source immediately',
    'Cool the burn with cool running water for 10-15 minutes',
    'Do not apply ice directly to the burned area',
    'Apply a loose sterile gauze bandage',
    'Call emergency services if the burn is severe or you are unsure',
    '',
    'SUMMARY:',
    'Immediate cooling and proper burn care are essential to prevent further tissue damage.',
    '',
    'RECOMMENDED_ACTION:',
    'Call emergency services immediately and cool the burn with running water',
    '',
    'DISCLAIMER:',
    'This is temporary AI first aid advice. Please consult a medical professional as soon as possible.',
].join('\n');

const tomcatBusyThreads = new Trend('tomcat_busy_threads');
const circuitOpenRate = new Rate('circuit_open_rate');
const fallbackRate = new Rate('fallback_rate');
const recoveryLag = new Trend('recovery_lag_ms');
const stubMiss = new Counter('stub_miss');

export const options = {
    scenarios: {
        firstAidLoad: {
            executor: 'constant-arrival-rate',
            rate: 4,
            timeUnit: '1s',
            duration: `${TEST_DURATION_SEC}s`,
            preAllocatedVUs: 50,
            maxVUs: 3000,
            exec: 'requestFirstAid',
        },
        faultInjection: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: 2,
            startTime: `${DEGRADE_START_SEC}s`,
            maxDuration: '8m',
            exec: 'injectFault',
        },
        canary: {
            executor: 'constant-arrival-rate',
            rate: 1,
            timeUnit: '2s',
            duration: `${TEST_DURATION_SEC}s`,
            preAllocatedVUs: 5,
            maxVUs: 50,
            exec: 'probeUnrelatedEndpoint',
        },
        monitor: {
            executor: 'constant-arrival-rate',
            rate: 1,
            timeUnit: '1s',
            duration: `${TEST_DURATION_SEC}s`,
            preAllocatedVUs: 1,
            maxVUs: 1,
            exec: 'observeServerState',
        },
    },
    thresholds: {
        'http_req_duration{scenario:canary}': ['p(95)<2000'],
        'http_req_failed{scenario:canary}': ['rate<0.05'],
        tomcat_busy_threads: ['max<200'],
        stub_miss: ['count<5'],
    },
    summaryTrendStats: ['min', 'med', 'avg', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

function setGeminiDelay(delayMs) {
    http.post(`${STUB_ADMIN_URL}/mappings/reset`, null);
    http.post(
        `${STUB_ADMIN_URL}/mappings`,
        JSON.stringify({
            priority: 0,
            request: {
                method: 'POST',
                urlPathPattern: '/v1beta/models/[^/]+:generateContent',
            },
            response: {
                status: 200,
                fixedDelayMilliseconds: delayMs,
                headers: { 'Content-Type': 'application/json' },
                jsonBody: {
                    candidates: [
                        {
                            content: { role: 'model', parts: [{ text: GEMINI_STUB_TEXT }] },
                            finishReason: 'STOP',
                            index: 0,
                        },
                    ],
                },
            },
        }),
        JSON_HEADERS
    );
}

function phaseAt(elapsedSec) {
    if (elapsedSec < DEGRADE_START_SEC) return 'normal';
    if (elapsedSec < DEGRADE_END_SEC) return 'degraded';
    return 'recovery';
}

export function setup() {
    setGeminiDelay(NORMAL_DELAY_MS);

    const probe = http.post(
        `${BASE_URL}/api/first-aid/advice`,
        JSON.stringify({
            symptomType: 'BURNS',
            symptomDetail: 'warmup',
            latitude: 37.5665,
            longitude: 126.978,
        }),
        { ...JSON_HEADERS, timeout: '30s' }
    );

    if (probe.status !== 200) {
        console.error(`[setup] warmup failed with status ${probe.status}`);
    }

    return { startedAt: Date.now() };
}

let injectionStep = 0;

export function injectFault() {
    const delayMs = injectionStep++ === 0 ? DEGRADED_DELAY_MS : NORMAL_DELAY_MS;
    console.log(`[fault] gemini delay -> ${delayMs}ms`);
    setGeminiDelay(delayMs);
    sleep(DEGRADE_END_SEC - DEGRADE_START_SEC);
}

export function requestFirstAid(data) {
    const phase = phaseAt((Date.now() - data.startedAt) / 1000);

    const res = http.post(
        `${BASE_URL}/api/first-aid/advice`,
        JSON.stringify({
            symptomType: SYMPTOM_TYPES[__ITER % SYMPTOM_TYPES.length],
            symptomDetail: '계단에서 넘어져 다리에서 피가 많이 납니다',
            latitude: 37.5665,
            longitude: 126.978,
        }),
        { ...JSON_HEADERS, timeout: '180s', tags: { phase } }
    );

    if (res.status === 599 || String(res.body).includes('STUB_MISS')) {
        stubMiss.add(1);
    }

    const servedByFallback =
        res.status === 200 && res.json('data.aiAvailable') === false;
    fallbackRate.add(servedByFallback, { phase });

    check(
        res,
        {
            'status is 200': (r) => r.status === 200,
            'actionable response': (r) => {
                const data = r.json('data');
                return data.aiAvailable === true
                    ? data.content !== null
                    : data.identificationResponse.emergencyContact !== null;
            },
        },
        { phase }
    );
}

export function probeUnrelatedEndpoint(data) {
    const phase = phaseAt((Date.now() - data.startedAt) / 1000);
    const res = http.get(`${BASE_URL}/`, { timeout: '30s', tags: { phase } });
    check(res, { 'canary alive': (r) => r.status === 200 });
}

let lastCircuitState = null;
let circuitOpenedAt = null;

export function observeServerState(data) {
    const elapsedSec = Math.round((Date.now() - data.startedAt) / 1000);

    const threadsRes = http.get(`${MGMT_URL}/actuator/metrics/tomcat.threads.busy`, {
        tags: { name: 'monitor' },
    });

    let busyThreads = null;
    if (threadsRes.status === 200) {
        busyThreads = threadsRes.json('measurements.0.value');
        tomcatBusyThreads.add(busyThreads);
    }

    const circuitRes = http.get(`${MGMT_URL}/actuator/circuitbreakers`, {
        tags: { name: 'monitor' },
    });

    let circuitState = 'N/A';
    if (circuitRes.status === 200) {
        circuitState = circuitRes.json('circuitBreakers.gemini.state') || 'UNKNOWN';
        circuitOpenRate.add(circuitState === 'OPEN' || circuitState === 'FORCED_OPEN');

        if (circuitState !== lastCircuitState) {
            console.log(`[circuit] ${lastCircuitState} -> ${circuitState} (t=${elapsedSec}s)`);

            if (circuitState === 'OPEN' && circuitOpenedAt === null) {
                circuitOpenedAt = Date.now();
            }

            if (
                circuitState === 'CLOSED' &&
                circuitOpenedAt !== null &&
                elapsedSec > DEGRADE_END_SEC
            ) {
                const lagMs = Date.now() - (data.startedAt + DEGRADE_END_SEC * 1000);
                recoveryLag.add(lagMs);
                console.log(`[circuit] recovered in ${Math.round(lagMs / 1000)}s`);
                circuitOpenedAt = null;
            }

            lastCircuitState = circuitState;
        }
    }

    console.log(
        `t=${elapsedSec}s phase=${phaseAt(elapsedSec)} busy=${busyThreads} circuit=${circuitState}`
    );
}

export function teardown() {
    setGeminiDelay(NORMAL_DELAY_MS);
    console.log('[teardown] gemini delay restored');
}
