package com.vitaltrip.vitaltrip.location.client;

import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchResponse;
import com.vitaltrip.vitaltrip.location.dto.Location;
import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FakeGoogleLocationClient {

    public GoogleTextSearchResponse textSearch(String keyword, Location location, Double radiusMeters, String language) {
        log.info("FakeGoogleLocationClient - keyword: {}, location: {}, radius: {}, language: {}",
                keyword, location, radiusMeters, language);

        return switch (keyword) {
            case "hospital emergency room medical center" -> createHospitalList();
            case "pharmacy" -> createPharmacyList();
            case "emergency room emergency department" -> createEmergencyList();
            default -> throw new IllegalArgumentException("Unsupported keyword: " + keyword);
        };
    }

    GoogleTextSearchRequest createRequestBody(String keyword, Location location, Double radiusMeters, String language) {
        GoogleTextSearchRequest.LocationBias locationBias = new GoogleTextSearchRequest.LocationBias(
                new GoogleTextSearchRequest.Circle(location, radiusMeters)
        );

        return new GoogleTextSearchRequest(keyword, locationBias, 15, language, true);
    }

    private GoogleTextSearchResponse createHospitalList() {
        List<GoogleTextSearchResponse.Place> hospitals = List.of(
                // 1km 이내 병원 2개
                createPlace("서울역 종합병원", "서울특별시 중구 한강대로 372",
                        new Location(37.5695, 126.9745), "+82-2-2128-5500",
                        createOpeningHours("일반병원"), "https://www.seoulstation-hospital.co.kr"),

                createPlace("중구의료원", "서울특별시 중구 소공로 120",
                        new Location(37.5635, 126.9850), "+82-2-3783-2000",
                        createOpeningHours("일반병원"), "https://www.junggu-medical.co.kr"),

                // 1km 초과 병원들 (기존 데이터 유지)
                createPlace("서울대학교병원", "서울특별시 종로구 대학로 101",
                        new Location(37.5796, 126.9968), "+82-2-2072-2114",
                        createOpeningHours("일반병원"), "https://www.snuh.org"),

                createPlace("세브란스병원", "서울특별시 서대문구 연세로 50-1",
                        new Location(37.5626, 126.9397), "+82-2-2228-5800",
                        createOpeningHours("일반병원"), "https://www.severance.healthcare"),

                createPlace("삼성서울병원", "서울특별시 강남구 일원로 81",
                        new Location(37.4885, 127.0854), "+82-2-3410-2114",
                        createOpeningHours("일반병원"), "https://www.samsunghospital.com"),

                createPlace("서울아산병원", "서울특별시 송파구 올림픽로43길 88",
                        new Location(37.5262, 127.1085), "+82-2-3010-3000",
                        createOpeningHours("24시간"), "https://www.amc.seoul.kr"),

                createPlace("고려대학교의료원", "서울특별시 성북구 고려대로 73",
                        new Location(37.5906, 127.0270), "+82-2-920-5114",
                        createOpeningHours("일반병원"), "https://www.kumc.or.kr"),

                createPlace("한양대학교병원", "서울특별시 성동구 왕십리로 222",
                        new Location(37.5583, 127.0436), "+82-2-2290-8114",
                        createOpeningHours("일반병원"), "https://seoul.hyumc.com"),

                createPlace("중앙대학교병원", "서울특별시 동작구 흑석로 102",
                        new Location(37.5038, 126.9574), "+82-2-6299-1114",
                        createOpeningHours("일반병원"), "https://www.caumc.or.kr"),

                createPlace("경희대학교병원", "서울특별시 동대문구 경희대로 23",
                        new Location(37.5945, 127.0516), "+82-2-958-8114",
                        createOpeningHours("일반병원"), "https://www.khmc.or.kr"),

                createPlace("이대서울병원", "서울특별시 서대문구 신촌로 260",
                        new Location(37.5564, 126.9365), "+82-2-6986-1616",
                        createOpeningHours("일반병원"), "https://seoul.eumc.ac.kr"),

                createPlace("강남세브란스병원", "서울특별시 강남구 언주로 211",
                        new Location(37.5193, 127.0473), "+82-2-2019-3000",
                        createOpeningHours("24시간"), "https://gs.iseverance.com"),

                createPlace("서울성모병원", "서울특별시 서초구 반포대로 222",
                        new Location(37.5016, 126.9990), "+82-2-2258-5776",
                        createOpeningHours("일반병원"), "https://www.cmcseoul.or.kr"),

                createPlace("분당서울대병원", "경기도 성남시 분당구 구미로 173번길 82",
                        new Location(37.3497, 127.1180), "+82-31-787-7114",
                        createOpeningHours("일반병원"), "https://www.snubh.org"),

                createPlace("보라매병원", "서울특별시 동작구 보라매로5길 20",
                        new Location(37.4937, 126.9246), "+82-2-870-2114",
                        createOpeningHours("일반병원"), "https://www.boramae.seoul.kr")
        );

        return new GoogleTextSearchResponse(hospitals);
    }

    private GoogleTextSearchResponse createPharmacyList() {
        List<GoogleTextSearchResponse.Place> pharmacies = List.of(
                // 1km 이내 약국 2개 (기존에서 1개 제거)
                createPlace("서울역 24시 온누리약국", "서울특별시 중구 한강대로 405",
                        new Location(37.5665, 126.9780), "+82-2-318-7700",
                        createOpeningHours("24시간"), "https://pharmacy1.co.kr"),

                createPlace("명동 참약국", "서울특별시 중구 명동길 26",
                        new Location(37.5636, 126.9834), "+82-2-771-5588",
                        createOpeningHours("일반약국"), "https://pharmacy2.co.kr"),

                // 1km 초과 약국들
                createPlace("강남역 24시 약국", "서울특별시 강남구 강남대로 390",
                        new Location(37.4979, 127.0276), "+82-2-538-1004",
                        createOpeningHours("24시간"), "https://pharmacy3.co.kr"),

                createPlace("홍대입구 청춘약국", "서울특별시 마포구 양화로 160",
                        new Location(37.5563, 126.9236), "+82-2-322-8800",
                        createOpeningHours("야간약국"), "https://pharmacy4.co.kr"),

                createPlace("종로 삼성약국", "서울특별시 종로구 종로 200",  // 좌표 수정하여 1km 밖으로
                        new Location(37.5704, 127.0000), "+82-2-2148-7700",
                        createOpeningHours("일반약국"), "https://pharmacy5.co.kr"),

                createPlace("신촌 건강드림약국", "서울특별시 서대문구 연세로 134",
                        new Location(37.5590, 126.9368), "+82-2-393-2200",
                        createOpeningHours("야간약국"), "https://pharmacy6.co.kr"),

                createPlace("잠실 온누리약국", "서울특별시 송파구 올림픽로 300",
                        new Location(37.5133, 127.1028), "+82-2-2147-1100",
                        createOpeningHours("일반약국"), "https://pharmacy7.co.kr"),

                createPlace("이태원 글로벌약국", "서울특별시 용산구 이태원로 200",
                        new Location(37.5349, 126.9947), "+82-2-797-5566",
                        createOpeningHours("야간약국"), "https://pharmacy8.co.kr"),

                createPlace("건대입구 365약국", "서울특별시 광진구 능동로 120",
                        new Location(37.5403, 127.0701), "+82-2-456-7788",
                        createOpeningHours("24시간"), "https://pharmacy9.co.kr"),

                createPlace("영등포 24시 약국", "서울특별시 영등포구 영등포로 160",
                        new Location(37.5185, 126.9085), "+82-2-2679-3300",
                        createOpeningHours("24시간"), "https://pharmacy10.co.kr")
        );

        return new GoogleTextSearchResponse(pharmacies);
    }

    private GoogleTextSearchResponse createEmergencyList() {
        List<GoogleTextSearchResponse.Place> emergencyRooms = List.of(
                // 1km 이내 응급실 1개
                createPlace("서울역 응급의료센터", "서울특별시 중구 한강대로 405",
                        new Location(37.5661, 126.9755), "+82-2-1588-7119",
                        createOpeningHours("24시간"), "https://www.seoul-emergency.co.kr"),

                // 1km 초과 응급실들
                createPlace("서울대병원 응급의료센터", "서울특별시 종로구 대학로 101",
                        new Location(37.5796, 126.9968), "+82-2-2072-1339",
                        createOpeningHours("24시간"), "https://www.snuh.org/emergency"),

                createPlace("아산병원 응급의료센터", "서울특별시 송파구 올림픽로43길 88",
                        new Location(37.5262, 127.1085), "+82-2-3010-1339",
                        createOpeningHours("24시간"), "https://www.amc.seoul.kr/emergency"),

                createPlace("삼성서울병원 응급실", "서울특별시 강남구 일원로 81",
                        new Location(37.4885, 127.0854), "+82-2-3410-1339",
                        createOpeningHours("24시간"), "https://www.samsunghospital.com/emergency"),

                createPlace("세브란스 응급의료센터", "서울특별시 서대문구 연세로 50-1",
                        new Location(37.5626, 126.9397), "+82-2-2228-1339",
                        createOpeningHours("24시간"), "https://www.severance.healthcare/emergency"),

                createPlace("강남세브란스 응급실", "서울특별시 강남구 언주로 211",
                        new Location(37.5193, 127.0473), "+82-2-2019-1339",
                        createOpeningHours("24시간"), "https://gs.iseverance.com/emergency"),

                createPlace("한양대병원 응급의료센터", "서울특별시 성동구 왕십리로 222",
                        new Location(37.5583, 127.0436), "+82-2-2290-1339",
                        createOpeningHours("24시간"), "https://seoul.hyumc.com/emergency")
        );

        return new GoogleTextSearchResponse(emergencyRooms);
    }

    private GoogleTextSearchResponse.Place createPlace(String name, String address, Location location,
                                                       String phone, GoogleTextSearchResponse.CurrentOpeningHours openingHours,
                                                       String websiteUrl) {
        GoogleTextSearchResponse.DisplayName displayName = new GoogleTextSearchResponse.DisplayName(name, "ko");
        return new GoogleTextSearchResponse.Place(displayName, address, location, phone, openingHours, websiteUrl);
    }

    private GoogleTextSearchResponse.CurrentOpeningHours createOpeningHours(String type) {
        List<String> weeklyHours = switch (type) {
            case "24시간" -> List.of(
                    "월요일: 24시간 영업", "화요일: 24시간 영업", "수요일: 24시간 영업",
                    "목요일: 24시간 영업", "금요일: 24시간 영업", "토요일: 24시간 영업", "일요일: 24시간 영업"
            );
            case "일반병원" -> List.of(
                    "월요일: 오전 8:30~오후 5:30", "화요일: 오전 8:30~오후 5:30", "수요일: 오전 8:30~오후 5:30",
                    "목요일: 오전 8:30~오후 5:30", "금요일: 오전 8:30~오후 5:30", "토요일: 오전 8:30~오후 12:30", "일요일: 휴무"
            );
            case "일반약국" -> List.of(
                    "월요일: 오전 9:00~오후 8:00", "화요일: 오전 9:00~오후 8:00", "수요일: 오전 9:00~오후 8:00",
                    "목요일: 오전 9:00~오후 8:00", "금요일: 오전 9:00~오후 8:00", "토요일: 오전 9:00~오후 6:00", "일요일: 휴무"
            );
            case "야간약국" -> List.of(
                    "월요일: 오전 9:00~오후 11:00", "화요일: 오전 9:00~오후 11:00", "수요일: 오전 9:00~오후 11:00",
                    "목요일: 오전 9:00~오후 11:00", "금요일: 오전 9:00~오후 11:00", "토요일: 오전 9:00~오후 9:00", "일요일: 오전 10:00~오후 6:00"
            );
            default -> List.of("운영시간 정보 없음");
        };

        return new GoogleTextSearchResponse.CurrentOpeningHours(true, null, weeklyHours);
    }
}
