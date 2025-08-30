package com.vitaltrip.vitaltrip.location.client;

import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchRequest;
import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchResponse;
import com.vitaltrip.vitaltrip.location.dto.Location;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FakeGoogleLocationClient {

    public GoogleTextSearchResponse textSearch(String keyword, Location location,
        Double radiusMeters, String language) {
        log.info("FakeGoogleLocationClient - keyword: {}, location: {}, radius: {}, language: {}",
            keyword, location, radiusMeters, language);

        return switch (keyword) {
            case "hospital emergency room medical center" -> createHospitalList();
            case "pharmacy" -> createPharmacyList();
            case "emergency room emergency department" -> createEmergencyList();
            default -> throw new IllegalArgumentException("Unsupported keyword: " + keyword);
        };
    }

    GoogleTextSearchRequest createRequestBody(String keyword, Location location,
        Double radiusMeters, String language) {
        GoogleTextSearchRequest.LocationBias locationBias = new GoogleTextSearchRequest.LocationBias(
            new GoogleTextSearchRequest.Circle(location, radiusMeters)
        );

        return new GoogleTextSearchRequest(keyword, locationBias, 15, language, true);
    }

    private GoogleTextSearchResponse createHospitalList() {
        List<GoogleTextSearchResponse.Place> hospitals = List.of(
            // Within 1km hospitals
            createPlace("Seoul Station General Hospital", "372 Hangang-daero, Jung-gu, Seoul",
                new Location(37.5695, 126.9745), "+82-2-2128-5500",
                createOpeningHours("general_hospital"), "https://www.seoulstation-hospital.co.kr"),

            createPlace("Jung-gu Medical Center", "120 Sogong-ro, Jung-gu, Seoul",
                new Location(37.5635, 126.9850), "+82-2-3783-2000",
                createOpeningHours("general_hospital"), "https://www.junggu-medical.co.kr"),

            // Hospitals beyond 1km
            createPlace("Seoul National University Hospital", "101 Daehak-ro, Jongno-gu, Seoul",
                new Location(37.5796, 126.9968), "+82-2-2072-2114",
                createOpeningHours("general_hospital"), "https://www.snuh.org"),

            createPlace("Severance Hospital", "50-1 Yonsei-ro, Seodaemun-gu, Seoul",
                new Location(37.5626, 126.9397), "+82-2-2228-5800",
                createOpeningHours("general_hospital"), "https://www.severance.healthcare"),

            createPlace("Samsung Seoul Hospital", "81 Irwon-ro, Gangnam-gu, Seoul",
                new Location(37.4885, 127.0854), "+82-2-3410-2114",
                createOpeningHours("general_hospital"), "https://www.samsunghospital.com"),

            createPlace("Asan Medical Center", "88 Olympic-ro 43-gil, Songpa-gu, Seoul",
                new Location(37.5262, 127.1085), "+82-2-3010-3000",
                createOpeningHours("24_hours"), "https://www.amc.seoul.kr"),

            createPlace("Korea University Medical Center", "73 Goryeodae-ro, Seongbuk-gu, Seoul",
                new Location(37.5906, 127.0270), "+82-2-920-5114",
                createOpeningHours("general_hospital"), "https://www.kumc.or.kr"),

            createPlace("Hanyang University Hospital", "222 Wangsimni-ro, Seongdong-gu, Seoul",
                new Location(37.5583, 127.0436), "+82-2-2290-8114",
                createOpeningHours("general_hospital"), "https://seoul.hyumc.com"),

            createPlace("Chung-Ang University Hospital", "102 Heukseok-ro, Dongjak-gu, Seoul",
                new Location(37.5038, 126.9574), "+82-2-6299-1114",
                createOpeningHours("general_hospital"), "https://www.caumc.or.kr"),

            createPlace("Kyung Hee University Hospital", "23 Kyungheedae-ro, Dongdaemun-gu, Seoul",
                new Location(37.5945, 127.0516), "+82-2-958-8114",
                createOpeningHours("general_hospital"), "https://www.khmc.or.kr"),

            createPlace("Ewha Womans University Seoul Hospital",
                "260 Sinchon-ro, Seodaemun-gu, Seoul",
                new Location(37.5564, 126.9365), "+82-2-6986-1616",
                createOpeningHours("general_hospital"), "https://seoul.eumc.ac.kr"),

            createPlace("Gangnam Severance Hospital", "211 Eonju-ro, Gangnam-gu, Seoul",
                new Location(37.5193, 127.0473), "+82-2-2019-3000",
                createOpeningHours("24_hours"), "https://gs.iseverance.com"),

            createPlace("Seoul St. Mary's Hospital", "222 Banpo-daero, Seocho-gu, Seoul",
                new Location(37.5016, 126.9990), "+82-2-2258-5776",
                createOpeningHours("general_hospital"), "https://www.cmcseoul.or.kr"),

            createPlace("Seoul National University Bundang Hospital",
                "82 Gumi-ro 173beon-gil, Bundang-gu, Seongnam-si, Gyeonggi-do",
                new Location(37.3497, 127.1180), "+82-31-787-7114",
                createOpeningHours("general_hospital"), "https://www.snubh.org"),

            createPlace("SMG-SNU Boramae Medical Center", "20 Boramae-ro 5-gil, Dongjak-gu, Seoul",
                new Location(37.4937, 126.9246), "+82-2-870-2114",
                createOpeningHours("general_hospital"), "https://www.boramae.seoul.kr")
        );

        return new GoogleTextSearchResponse(hospitals);
    }

    private GoogleTextSearchResponse createPharmacyList() {
        List<GoogleTextSearchResponse.Place> pharmacies = List.of(
            // Within 1km pharmacies
            createPlace("Seoul Station 24h Onnuri Pharmacy", "405 Hangang-daero, Jung-gu, Seoul",
                new Location(37.5665, 126.9780), "+82-2-318-7700",
                createOpeningHours("24_hours"), "https://pharmacy1.co.kr"),

            createPlace("Myeongdong Cham Pharmacy", "26 Myeongdong-gil, Jung-gu, Seoul",
                new Location(37.5636, 126.9834), "+82-2-771-5588",
                createOpeningHours("general_pharmacy"), "https://pharmacy2.co.kr"),

            // Pharmacies beyond 1km
            createPlace("Gangnam Station 24h Pharmacy", "390 Gangnam-daero, Gangnam-gu, Seoul",
                new Location(37.4979, 127.0276), "+82-2-538-1004",
                createOpeningHours("24_hours"), "https://pharmacy3.co.kr"),

            createPlace("Hongik Univ. Youth Pharmacy", "160 Yanghwa-ro, Mapo-gu, Seoul",
                new Location(37.5563, 126.9236), "+82-2-322-8800",
                createOpeningHours("night_pharmacy"), "https://pharmacy4.co.kr"),

            createPlace("Jongno Samsung Pharmacy", "200 Jongno, Jongno-gu, Seoul",
                new Location(37.5704, 127.0000), "+82-2-2148-7700",
                createOpeningHours("general_pharmacy"), "https://pharmacy5.co.kr"),

            createPlace("Sinchon Health Dream Pharmacy", "134 Yonsei-ro, Seodaemun-gu, Seoul",
                new Location(37.5590, 126.9368), "+82-2-393-2200",
                createOpeningHours("night_pharmacy"), "https://pharmacy6.co.kr"),

            createPlace("Jamsil Onnuri Pharmacy", "300 Olympic-ro, Songpa-gu, Seoul",
                new Location(37.5133, 127.1028), "+82-2-2147-1100",
                createOpeningHours("general_pharmacy"), "https://pharmacy7.co.kr"),

            createPlace("Itaewon Global Pharmacy", "200 Itaewon-ro, Yongsan-gu, Seoul",
                new Location(37.5349, 126.9947), "+82-2-797-5566",
                createOpeningHours("night_pharmacy"), "https://pharmacy8.co.kr"),

            createPlace("Konkuk Univ. 365 Pharmacy", "120 Neungdong-ro, Gwangjin-gu, Seoul",
                new Location(37.5403, 127.0701), "+82-2-456-7788",
                createOpeningHours("24_hours"), "https://pharmacy9.co.kr"),

            createPlace("Yeongdeungpo 24h Pharmacy", "160 Yeongdeungpo-ro, Yeongdeungpo-gu, Seoul",
                new Location(37.5185, 126.9085), "+82-2-2679-3300",
                createOpeningHours("24_hours"), "https://pharmacy10.co.kr")
        );

        return new GoogleTextSearchResponse(pharmacies);
    }

    private GoogleTextSearchResponse createEmergencyList() {
        List<GoogleTextSearchResponse.Place> emergencyRooms = List.of(
            // Within 1km emergency room
            createPlace("Seoul Station Emergency Medical Center",
                "405 Hangang-daero, Jung-gu, Seoul",
                new Location(37.5661, 126.9755), "+82-2-1588-7119",
                createOpeningHours("24_hours"), "https://www.seoul-emergency.co.kr"),

            // Emergency rooms beyond 1km
            createPlace("Seoul National University Hospital Emergency Center",
                "101 Daehak-ro, Jongno-gu, Seoul",
                new Location(37.5796, 126.9968), "+82-2-2072-1339",
                createOpeningHours("24_hours"), "https://www.snuh.org/emergency"),

            createPlace("Asan Medical Center Emergency Department",
                "88 Olympic-ro 43-gil, Songpa-gu, Seoul",
                new Location(37.5262, 127.1085), "+82-2-3010-1339",
                createOpeningHours("24_hours"), "https://www.amc.seoul.kr/emergency"),

            createPlace("Samsung Seoul Hospital Emergency Room", "81 Irwon-ro, Gangnam-gu, Seoul",
                new Location(37.4885, 127.0854), "+82-2-3410-1339",
                createOpeningHours("24_hours"), "https://www.samsunghospital.com/emergency"),

            createPlace("Severance Hospital Emergency Center",
                "50-1 Yonsei-ro, Seodaemun-gu, Seoul",
                new Location(37.5626, 126.9397), "+82-2-2228-1339",
                createOpeningHours("24_hours"), "https://www.severance.healthcare/emergency"),

            createPlace("Gangnam Severance Hospital Emergency Room",
                "211 Eonju-ro, Gangnam-gu, Seoul",
                new Location(37.5193, 127.0473), "+82-2-2019-1339",
                createOpeningHours("24_hours"), "https://gs.iseverance.com/emergency"),

            createPlace("Hanyang University Hospital Emergency Center",
                "222 Wangsimni-ro, Seongdong-gu, Seoul",
                new Location(37.5583, 127.0436), "+82-2-2290-1339",
                createOpeningHours("24_hours"), "https://seoul.hyumc.com/emergency")
        );

        return new GoogleTextSearchResponse(emergencyRooms);
    }

    private GoogleTextSearchResponse.Place createPlace(String name, String address,
        Location location,
        String phone, GoogleTextSearchResponse.CurrentOpeningHours openingHours,
        String websiteUrl) {
        GoogleTextSearchResponse.DisplayName displayName = new GoogleTextSearchResponse.DisplayName(
            name, "en");
        return new GoogleTextSearchResponse.Place(displayName, address, location, phone,
            openingHours, websiteUrl);
    }

    private GoogleTextSearchResponse.CurrentOpeningHours createOpeningHours(String type) {
        List<String> weeklyHours = switch (type) {
            case "24_hours" -> List.of(
                "Monday: Open 24 hours", "Tuesday: Open 24 hours", "Wednesday: Open 24 hours",
                "Thursday: Open 24 hours", "Friday: Open 24 hours", "Saturday: Open 24 hours",
                "Sunday: Open 24 hours"
            );
            case "general_hospital" -> List.of(
                "Monday: 8:30 AM–5:30 PM", "Tuesday: 8:30 AM–5:30 PM", "Wednesday: 8:30 AM–5:30 PM",
                "Thursday: 8:30 AM–5:30 PM", "Friday: 8:30 AM–5:30 PM",
                "Saturday: 8:30 AM–12:30 PM", "Sunday: Closed"
            );
            case "general_pharmacy" -> List.of(
                "Monday: 9:00 AM–8:00 PM", "Tuesday: 9:00 AM–8:00 PM", "Wednesday: 9:00 AM–8:00 PM",
                "Thursday: 9:00 AM–8:00 PM", "Friday: 9:00 AM–8:00 PM", "Saturday: 9:00 AM–6:00 PM",
                "Sunday: Closed"
            );
            case "night_pharmacy" -> List.of(
                "Monday: 9:00 AM–11:00 PM", "Tuesday: 9:00 AM–11:00 PM",
                "Wednesday: 9:00 AM–11:00 PM",
                "Thursday: 9:00 AM–11:00 PM", "Friday: 9:00 AM–11:00 PM",
                "Saturday: 9:00 AM–9:00 PM", "Sunday: 10:00 AM–6:00 PM"
            );
            default -> List.of("Hours not available");
        };

        return new GoogleTextSearchResponse.CurrentOpeningHours(true, null, weeklyHours);
    }
}
