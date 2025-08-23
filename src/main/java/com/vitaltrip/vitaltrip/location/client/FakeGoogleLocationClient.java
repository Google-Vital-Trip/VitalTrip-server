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
        log.info("FakeGoogleLocationClient - textSearch called with keyword: {}, location: {}, radius: {}, language: {}",
                keyword, location, radiusMeters, language);

        return createFakeHospitalResponse();
    }

    GoogleTextSearchRequest createRequestBody(String keyword, Location location, Double radiusMeters, String language) {
        GoogleTextSearchRequest.LocationBias locationBias = new GoogleTextSearchRequest.LocationBias(
                new GoogleTextSearchRequest.Circle(location, radiusMeters)
        );

        return new GoogleTextSearchRequest(
                keyword,
                locationBias,
                15,
                language,
                true
        );
    }

    private GoogleTextSearchResponse createFakeHospitalResponse() {
        List<GoogleTextSearchResponse.Place> hospitals = List.of(
                // 기준점 (서울역) 근처: 37.5665, 126.9780

                // 1. 서울역 바로 근처 (500m 내)
                createHospital("서울역 응급의료센터", "서울특별시 중구 한강대로 405",
                        new Location(37.5661, 126.9755), "+82-2-1588-7119", true, "24시간"),

                // 2. 명동 방향 (1km)
                createHospital("중구보건소 부설 의료원", "서울특별시 중구 다산로 117",
                        new Location(37.5640, 126.9850), "+82-2-2266-1471", true, "일반"),

                // 3. 시청 방향 (1.5km)
                createHospital("서울시립 중랑의료원", "서울특별시 중구 세종대로 110",
                        new Location(37.5663, 126.9779), "+82-2-2120-3114", true, "응급실"),

                // 4. 남대문 방향 (1km)
                createHospital("남대문 종합병원", "서울특별시 중구 회현동1가 100-1",
                        new Location(37.5584, 126.9756), "+82-2-752-0081", true, "일반"),

                // 5. 종로 방향 (2km)
                createHospital("종로구 보건의료원", "서울특별시 종로구 종로 1",
                        new Location(37.5704, 126.9826), "+82-2-2148-9000", true, "24시간"),

                // 6. 마포 방향 (3km)
                createHospital("서울역삼성병원", "서울특별시 마포구 마포대로 53",
                        new Location(37.5449, 126.9516), "+82-2-2077-7000", true, "종합"),

                // 7. 용산 방향 (2.5km)
                createHospital("용산구민의료원", "서울특별시 용산구 이촌로 177",
                        new Location(37.5347, 126.9678), "+82-2-2199-7000", true, "일반"),

                // 8. 홍대 방향 (4km)
                createHospital("홍대 세브란스병원", "서울특별시 마포구 양화로 55",
                        new Location(37.5510, 126.9227), "+82-2-312-0065", true, "응급실"),

                // 9. 강남 방향 (4.5km)
                createHospital("한강성심병원 서울역점", "서울특별시 영등포구 여의대로 10",
                        new Location(37.5219, 126.9245), "+82-2-2639-5000", true, "종합"),

                // 10. 동대문 방향 (3.5km)
                createHospital("동대문구 보건의료원", "서울특별시 동대문구 천호대로 145",
                        new Location(37.5844, 127.0139), "+82-2-2127-4500", true, "24시간"),

                // 11. 성동 방향 (3km)
                createHospital("성동구민 종합병원", "서울특별시 성동구 왕십리로 15",
                        new Location(37.5635, 127.0286), "+82-2-2299-1114", true, "일반"),

                // 12. 서대문 방향 (3km)
                createHospital("신촌 세브란스병원 분원", "서울특별시 서대문구 연세로 134",
                        new Location(37.5590, 126.9368), "+82-2-393-4114", true, "응급실"),

                // 13. 영등포 방향 (4km)
                createHospital("영등포성모병원", "서울특별시 영등포구 여의도동 62",
                        new Location(37.5185, 126.9366), "+82-2-3779-1000", true, "종합"),

                // 14. 광화문 방향 (2km)
                createHospital("서울대학교병원 분원", "서울특별시 종로구 대학로 28",
                        new Location(37.5758, 126.9768), "+82-2-2072-0505", true, "24시간"),

                // 15. 이태원 방향 (3.5km)
                createHospital("용산 국제병원", "서울특별시 용산구 이태원로 112",
                        new Location(37.5349, 126.9947), "+82-2-797-1004", true, "응급실")
        );

        return new GoogleTextSearchResponse(hospitals);
    }

    private GoogleTextSearchResponse.Place createHospital(String name, String address,
                                                          Location location, String phone, boolean isOpen, String type) {

        GoogleTextSearchResponse.DisplayName displayName = new GoogleTextSearchResponse.DisplayName(name, "ko");

        List<String> weeklyHours = switch (type) {
            case "24시간" -> List.of(
                    "월요일: 24시간 영업", "화요일: 24시간 영업", "수요일: 24시간 영업",
                    "목요일: 24시간 영업", "금요일: 24시간 영업", "토요일: 24시간 영업", "일요일: 24시간 영업"
            );
            case "응급실" -> List.of(
                    "월요일: 오전 9:00~오후 10:00", "화요일: 오전 9:00~오후 10:00", "수요일: 오전 9:00~오후 10:00",
                    "목요일: 오전 9:00~오후 10:00", "금요일: 오전 9:00~오후 10:00", "토요일: 오전 9:00~오후 6:00", "일요일: 오전 10:00~오후 4:00"
            );
            case "종합" -> List.of(
                    "월요일: 오전 8:00~오후 6:00", "화요일: 오전 8:00~오후 6:00", "수요일: 오전 8:00~오후 6:00",
                    "목요일: 오전 8:00~오후 6:00", "금요일: 오전 8:00~오후 6:00", "토요일: 오전 8:00~오후 1:00", "일요일: 휴무"
            );
            default -> List.of( // "일반"
                    "월요일: 오전 9:00~오후 6:00", "화요일: 오전 9:00~오후 6:00", "수요일: 오전 9:00~오후 6:00",
                    "목요일: 오전 9:00~오후 6:00", "금요일: 오전 9:00~오후 6:00", "토요일: 오전 9:00~오후 1:00", "일요일: 휴무"
            );
        };

        GoogleTextSearchResponse.CurrentOpeningHours openingHours = new GoogleTextSearchResponse.CurrentOpeningHours(
                isOpen, null, weeklyHours
        );

        String websiteUrl = "https://hospital" + Math.abs(name.hashCode()) + ".co.kr";

        return new GoogleTextSearchResponse.Place(
                displayName, address, location, phone, openingHours, websiteUrl
        );
    }
}
