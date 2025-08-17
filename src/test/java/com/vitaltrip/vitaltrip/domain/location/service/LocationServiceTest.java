package com.vitaltrip.vitaltrip.domain.location.service;

import com.vitaltrip.vitaltrip.domain.location.client.GoogleLocationClient;
import com.vitaltrip.vitaltrip.domain.location.dto.GoogleTextSearchResponse;
import com.vitaltrip.vitaltrip.domain.location.dto.Location;
import com.vitaltrip.vitaltrip.domain.location.dto.NearbyPlaceRequest;
import com.vitaltrip.vitaltrip.domain.location.dto.NearbyPlaceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LocationService 테스트")
class LocationServiceTest {

    @Mock
    private GoogleLocationClient googleLocationClient;

    @InjectMocks
    private LocationService locationService;

    private NearbyPlaceRequest testRequest;

    @BeforeEach
    void setUp() {
        Location testLocation = new Location(37.5665, 126.9780);
        testRequest = new NearbyPlaceRequest(
                testLocation,
                "hospital",
                10000.0,
                "ko"
        );
    }

    @Nested
    @DisplayName("기본 검색 기능 테스트")
    class BasicSearchTest {

        @Test
        @DisplayName("정상적인 병원 검색이 성공해야 한다")
        void searchNearbyPlaces_Success() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithHospitals());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("서울대학교병원");
            assertThat(result.get(1).name()).isEqualTo("세브란스병원");
        }

        @Test
        @DisplayName("Google API 응답이 null인 경우 빈 리스트를 반환해야 한다")
        void searchNearbyPlaces_NullResponse_ReturnsEmptyList() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(null);

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("places가 null인 경우 빈 리스트를 반환해야 한다")
        void searchNearbyPlaces_NullPlaces_ReturnsEmptyList() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(new GoogleTextSearchResponse(null));

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("필터링 테스트")
    class FilteringTest {

        @Test
        @DisplayName("동물병원이 필터링되어야 한다")
        void animalHospitals_ShouldBeFiltered() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithAnimalHospitals());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().name()).isEqualTo("서울대학교병원");
            assertThat(result).noneMatch(place -> place.name().contains("동물병원"));
        }

        @Test
        @DisplayName("마사지샵이 필터링되어야 한다")
        void massageShops_ShouldBeFiltered() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithMassageShops());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().name()).isEqualTo("서울대학교병원");
            assertThat(result).noneMatch(place -> place.name().contains("마사지"));
        }

    }

    @Nested
    @DisplayName("거리 및 정렬 테스트")
    class DistanceAndSortingTest {

        @Test
        @DisplayName("10km 반경을 초과하는 결과가 필터링되어야 한다")
        void resultsOutside10km_ShouldBeFiltered() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithVariousDistances());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(place -> place.distance() <= 10000.0);
            assertThat(result).noneMatch(place -> place.name().equals("원거리 병원"));
        }

        @Test
        @DisplayName("결과가 거리순으로 정렬되어야 한다")
        void results_ShouldBeSortedByDistance() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithUnsortedDistances());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).name()).isEqualTo("가까운 병원");
            assertThat(result.get(1).name()).isEqualTo("중간 거리 병원");
            assertThat(result.get(2).name()).isEqualTo("먼 종합병원");

            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).distance()).isLessThanOrEqualTo(result.get(i + 1).distance());
            }
        }

        @Test
        @DisplayName("최대 15개 결과로 제한되어야 한다")
        void results_ShouldBeLimitedTo15() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithManyHospitals());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(15);
        }
    }

    @Nested
    @DisplayName("운영시간 테스트")
    class OpeningHoursTest {

        @Test
        @DisplayName("운영시간 정보가 정상적으로 추출되어야 한다")
        void openingHours_ShouldBeExtractedCorrectly() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithOpeningHours());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(1);
            NearbyPlaceResponse place = result.getFirst();
            assertThat(place.openNow()).isTrue();
            assertThat(place.openingHours()).containsExactly("월요일: 24시간 영업");
        }

        @Test
        @DisplayName("운영시간 정보가 null인 경우 안전하게 처리되어야 한다")
        void openingHours_ShouldHandleNullSafely() {
            given(googleLocationClient.textSearch(anyString(), any(Location.class), anyDouble(), anyString()))
                    .willReturn(createMockResponseWithNullOpeningHours());

            List<NearbyPlaceResponse> result = locationService.searchNearbyPlaces(testRequest);

            assertThat(result).hasSize(1);
            NearbyPlaceResponse place = result.getFirst();
            assertThat(place.openNow()).isFalse();
            assertThat(place.openingHours()).isEmpty();
        }
    }

    private GoogleTextSearchResponse createMockResponseWithHospitals() {
        GoogleTextSearchResponse.DisplayName displayName1 = new GoogleTextSearchResponse.DisplayName("서울대학교병원", "ko");
        GoogleTextSearchResponse.DisplayName displayName2 = new GoogleTextSearchResponse.DisplayName("세브란스병원", "ko");

        GoogleTextSearchResponse.Place place1 = new GoogleTextSearchResponse.Place(
                displayName1, "서울시 종로구 대학로 101",
                new Location(37.5796, 126.9968), "+82-2-2072-2114", null, "https://snuh.org"
        );

        GoogleTextSearchResponse.Place place2 = new GoogleTextSearchResponse.Place(
                displayName2, "서울시 서대문구 연세로 50-1",
                new Location(37.5626, 126.9397), "+82-2-2228-5800", null, "https://severance.healthcare"
        );

        return new GoogleTextSearchResponse(List.of(place1, place2));
    }

    private GoogleTextSearchResponse createMockResponseWithAnimalHospitals() {
        GoogleTextSearchResponse.DisplayName hospital = new GoogleTextSearchResponse.DisplayName("서울대학교병원", "ko");
        GoogleTextSearchResponse.DisplayName animalHospital = new GoogleTextSearchResponse.DisplayName("강남 동물병원", "ko");

        GoogleTextSearchResponse.Place place1 = new GoogleTextSearchResponse.Place(
                hospital, "서울시 종로구", new Location(37.5796, 126.9968), null, null, null
        );
        GoogleTextSearchResponse.Place place2 = new GoogleTextSearchResponse.Place(
                animalHospital, "서울시 강남구", new Location(37.5000, 126.9000), null, null, null
        );

        return new GoogleTextSearchResponse(List.of(place1, place2));
    }

    private GoogleTextSearchResponse createMockResponseWithMassageShops() {
        GoogleTextSearchResponse.DisplayName hospital = new GoogleTextSearchResponse.DisplayName("서울대학교병원", "ko");
        GoogleTextSearchResponse.DisplayName massage = new GoogleTextSearchResponse.DisplayName("힐링 마사지", "ko");

        GoogleTextSearchResponse.Place place1 = new GoogleTextSearchResponse.Place(
                hospital, "서울시 종로구", new Location(37.5796, 126.9968), null, null, null
        );
        GoogleTextSearchResponse.Place place2 = new GoogleTextSearchResponse.Place(
                massage, "서울시 강남구", new Location(37.5000, 126.9000), null, null, null
        );

        return new GoogleTextSearchResponse(List.of(place1, place2));
    }

    private GoogleTextSearchResponse createMockResponseWithVariousDistances() {
        GoogleTextSearchResponse.DisplayName close = new GoogleTextSearchResponse.DisplayName("가까운 병원", "ko");
        GoogleTextSearchResponse.DisplayName medium = new GoogleTextSearchResponse.DisplayName("중간 거리 병원", "ko");
        GoogleTextSearchResponse.DisplayName far = new GoogleTextSearchResponse.DisplayName("원거리 병원", "ko");

        GoogleTextSearchResponse.Place place1 = new GoogleTextSearchResponse.Place(
                close, "서울시 중구", new Location(37.5666, 126.9781), null, null, null
        );
        GoogleTextSearchResponse.Place place2 = new GoogleTextSearchResponse.Place(
                medium, "서울시 종로구", new Location(37.5700, 126.9800), null, null, null
        );
        GoogleTextSearchResponse.Place place3 = new GoogleTextSearchResponse.Place(
                far, "부산시", new Location(35.1796, 129.0756), null, null, null
        );

        return new GoogleTextSearchResponse(List.of(place1, place2, place3));
    }

    private GoogleTextSearchResponse createMockResponseWithUnsortedDistances() {
        GoogleTextSearchResponse.DisplayName far = new GoogleTextSearchResponse.DisplayName("먼 종합병원", "ko");
        GoogleTextSearchResponse.DisplayName close = new GoogleTextSearchResponse.DisplayName("가까운 병원", "ko");
        GoogleTextSearchResponse.DisplayName medium = new GoogleTextSearchResponse.DisplayName("중간 거리 병원", "ko");

        GoogleTextSearchResponse.Place place1 = new GoogleTextSearchResponse.Place(
                far, "서울시 강남구", new Location(37.5300, 126.9500), null, null, null
        );
        GoogleTextSearchResponse.Place place2 = new GoogleTextSearchResponse.Place(
                close, "서울시 중구", new Location(37.5666, 126.9781), null, null, null
        );
        GoogleTextSearchResponse.Place place3 = new GoogleTextSearchResponse.Place(
                medium, "서울시 종로구", new Location(37.5700, 126.9800), null, null, null
        );

        return new GoogleTextSearchResponse(List.of(place1, place2, place3));
    }

    private GoogleTextSearchResponse createMockResponseWithManyHospitals() {
        List<GoogleTextSearchResponse.Place> places = new java.util.ArrayList<>();

        for (int i = 0; i < 20; i++) {
            GoogleTextSearchResponse.DisplayName displayName =
                    new GoogleTextSearchResponse.DisplayName("병원" + i, "ko");

            GoogleTextSearchResponse.Place place = new GoogleTextSearchResponse.Place(
                    displayName, "서울시 주소" + i,
                    new Location(37.5665 + (i * 0.001), 126.9780 + (i * 0.001)),
                    null, null, null
            );
            places.add(place);
        }

        return new GoogleTextSearchResponse(places);
    }

    private GoogleTextSearchResponse createMockResponseWithOpeningHours() {
        GoogleTextSearchResponse.DisplayName displayName = new GoogleTextSearchResponse.DisplayName("24시간 병원", "ko");
        GoogleTextSearchResponse.CurrentOpeningHours openingHours =
                new GoogleTextSearchResponse.CurrentOpeningHours(
                        true, null, List.of("월요일: 24시간 영업")
                );

        GoogleTextSearchResponse.Place place = new GoogleTextSearchResponse.Place(
                displayName, "서울시 중구", new Location(37.5665, 126.9780),
                null, openingHours, null
        );

        return new GoogleTextSearchResponse(List.of(place));
    }

    private GoogleTextSearchResponse createMockResponseWithNullOpeningHours() {
        GoogleTextSearchResponse.DisplayName displayName = new GoogleTextSearchResponse.DisplayName("일반 병원", "ko");

        GoogleTextSearchResponse.Place place = new GoogleTextSearchResponse.Place(
                displayName, "서울시 중구", new Location(37.5665, 126.9780),
                null, null, null
        );

        return new GoogleTextSearchResponse(List.of(place));
    }
}
