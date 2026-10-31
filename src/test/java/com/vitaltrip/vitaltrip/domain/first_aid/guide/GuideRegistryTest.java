package com.vitaltrip.vitaltrip.domain.first_aid.guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.EnumMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GuideRegistryTest {

    @Test
    @DisplayName("ID로 가이드를 조회한다")
    void get() {
        GuideRegistry registry = new GuideRegistry(allGuides());

        assertThat(registry.get(GuideId.BLEEDING).id()).isEqualTo(GuideId.BLEEDING);
    }

    @Test
    @DisplayName("전체 목록은 GuideId 선언 순서를 따른다")
    void allInDeclarationOrder() {
        GuideRegistry registry = new GuideRegistry(allGuides());

        assertThat(registry.all()).extracting(Guide::id).containsExactly(GuideId.values());
    }

    @Test
    @DisplayName("가이드가 빠진 ID가 있으면 생성에 실패한다")
    void missingGuide() {
        Map<GuideId, Guide> guides = allGuides();
        guides.remove(GuideId.STROKE);

        assertThatThrownBy(() -> new GuideRegistry(guides))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("STROKE");
    }

    private static Map<GuideId, Guide> allGuides() {
        Map<GuideId, Guide> guides = new EnumMap<>(GuideId.class);
        for (GuideId id : GuideId.values()) {
            guides.put(id, new Guide(id, 1, null, null, null, null, null, null, null));
        }
        return guides;
    }
}
