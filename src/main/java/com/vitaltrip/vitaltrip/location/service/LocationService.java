package com.vitaltrip.vitaltrip.location.service;

import com.vitaltrip.vitaltrip.location.client.BigDataCloudClient;
import com.vitaltrip.vitaltrip.location.client.FakeGoogleLocationClient;
import com.vitaltrip.vitaltrip.location.dto.BigDataCloudResponse;
import com.vitaltrip.vitaltrip.location.dto.CountryIdentificationRequest;
import com.vitaltrip.vitaltrip.location.dto.CountryIdentificationResponse;
import com.vitaltrip.vitaltrip.location.dto.GoogleTextSearchResponse;
import com.vitaltrip.vitaltrip.location.dto.Location;
import com.vitaltrip.vitaltrip.location.dto.NearbyPlaceRequest;
import com.vitaltrip.vitaltrip.location.dto.NearbyPlaceResponse;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final FakeGoogleLocationClient googleLocationClient;
    private final BigDataCloudClient bigDataCloudClient;
    private final EmergencyContactService emergencyContactService;

    public List<NearbyPlaceResponse> searchNearbyPlaces(NearbyPlaceRequest request) {

        String searchQuery = buildUniversalSearchQuery(request.type());

        GoogleTextSearchResponse response = googleLocationClient.textSearch(
            searchQuery, request.location(), request.radius(), request.language());

        return convertToNearbyPlaceResponse(response, request.location(), request.radius());
    }

    public CountryIdentificationResponse identifyCountry(CountryIdentificationRequest request) {

        BigDataCloudResponse response = bigDataCloudClient.reverseGeocode(
            request.latitude(), request.longitude());

        return CountryIdentificationResponse.from(
            response, request.latitude(), request.longitude(),
            emergencyContactService.getEmergencyContact(
                response.countryCode()));
    }

    private String buildUniversalSearchQuery(String type) {
        return switch (type) {
            case "hospital" -> "hospital emergency room medical center";
            case "emergency" -> "emergency room emergency department";
            default -> type;
        };
    }

    private List<NearbyPlaceResponse> convertToNearbyPlaceResponse(
        GoogleTextSearchResponse response, Location userLocation, Double searchRadius) {

        if (response == null || response.places() == null) {
            return List.of();
        }

        return response.places().stream()
            .map(place -> convertSinglePlace(place, userLocation))
            .filter(place -> place.distance() <= searchRadius)
            .filter(this::isValidMedicalFacility)
            .sorted(Comparator.comparingDouble(NearbyPlaceResponse::distance))
            .limit(15)
            .collect(Collectors.toList());
    }

    private boolean isValidMedicalFacility(NearbyPlaceResponse place) {
        String name = place.name().toLowerCase();
        String address = place.address() != null ? place.address().toLowerCase() : "";

        List<String> excludeKeywords = List.of(
            // 동물 관련
            "animal hospital", "veterinary", "vet clinic", "pet clinic", "pet hospital",
            "동물병원", "동물의료", "수의사", "애니멀", "펫",
            "動物病院", "獣医", "ペット", "宠物医院", "兽医",

            // 마사지/스파/미용
            "massage", "spa", "sauna", "nail", "beauty", "hair salon",
            "마사지", "안마", "스파", "사우나", "네일", "미용", "헤어",
            "マッサージ", "スパ", "サウナ", "ネイル", "美容", "ヘア",
            "按摩", "水疗", "桑拿", "美甲", "美容", "美发",

            // 종교 시설
            "temple", "church", "cathedral", "mosque", "synagogue",
            "사찰", "절", "교회", "성당", "모스크",
            "寺", "教会", "大聖堂", "モスク", "寺庙", "教堂", "清真寺",

            // 요양/재활 시설
            "nursing home", "care facility", "rehabilitation", "recovery center",
            "요양병원", "요양원", "재활병원", "회복병원",
            "介護施設", "リハビリ", "養護施設", "康复中心", "护理院",

            // 기타 비의료 시설
            "cafe", "restaurant", "hotel", "motel", "accommodation",
            "카페", "레스토랑", "음식점", "호텔", "모텔",
            "カフェ", "レストラン", "ホテル", "咖啡", "餐厅", "酒店"
        );

        boolean shouldExclude = excludeKeywords.stream()
            .anyMatch(keyword -> name.contains(keyword) || address.contains(keyword));

        return !shouldExclude;
    }

    private NearbyPlaceResponse convertSinglePlace(
        GoogleTextSearchResponse.Place place, Location userLocation) {

        double distance = calculateDistance(
            userLocation.latitude(), userLocation.longitude(),
            place.location().latitude(), place.location().longitude()
        );

        boolean openNow = extractOpenNowStatus(place);
        List<String> openingHours = extractOpeningHours(place);

        return new NearbyPlaceResponse(
            place.displayName() != null ? place.displayName().text() : "정보 없음",
            place.formattedAddress(),
            place.nationalPhoneNumber(),
            place.location().latitude(),
            place.location().longitude(),
            Math.round(distance * 10.0) / 10.0,
            openNow,
            openingHours,
            place.websiteUri()
        );
    }

    private boolean extractOpenNowStatus(GoogleTextSearchResponse.Place place) {
        try {
            return place.currentOpeningHours() != null &&
                place.currentOpeningHours().openNow() != null &&
                place.currentOpeningHours().openNow();
        } catch (Exception e) {
            return false;
        }
    }

    private List<String> extractOpeningHours(GoogleTextSearchResponse.Place place) {
        try {
            return place.currentOpeningHours() != null &&
                place.currentOpeningHours().weekdayDescriptions() != null
                ? place.currentOpeningHours().weekdayDescriptions()
                : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000;
    }
}
