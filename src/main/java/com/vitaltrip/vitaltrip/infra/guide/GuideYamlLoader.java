package com.vitaltrip.vitaltrip.infra.guide;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.Guide;
import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideId;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.Resource;

public class GuideYamlLoader {

    private final ObjectMapper mapper;

    GuideYamlLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static GuideYamlLoader create() {
        return new GuideYamlLoader(YAMLMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .build());
    }

    public Map<GuideId, Guide> load(Resource[] resources) {
        if (resources == null || resources.length == 0) {
            throw new IllegalStateException("가이드 파일을 찾을 수 없습니다");
        }

        Map<GuideId, Guide> guides = new EnumMap<>(GuideId.class);
        Map<GuideId, String> fileNames = new EnumMap<>(GuideId.class);

        for (Resource resource : resources) {
            String fileName = resource.getFilename();
            Guide guide = read(resource, fileName);

            if (guide.id() == null) {
                throw new IllegalStateException(fileName + ": id가 없습니다");
            }
            String expected = guide.id().name().toLowerCase(Locale.ROOT) + ".yml";
            if (!expected.equals(fileName)) {
                throw new IllegalStateException(
                        fileName + ": 파일명은 id와 같아야 합니다 (" + expected + ")");
            }
            String previous = fileNames.putIfAbsent(guide.id(), fileName);
            if (previous != null) {
                throw new IllegalStateException(
                        fileName + ": " + previous + "와 id가 같습니다 (" + guide.id() + ")");
            }
            guides.put(guide.id(), guide);
        }
        return guides;
    }

    private Guide read(Resource resource, String fileName) {
        try (InputStream in = resource.getInputStream()) {
            return mapper.readValue(in, Guide.class);
        } catch (IOException e) {
            throw new IllegalStateException(fileName + ": 가이드를 읽을 수 없습니다 - " + e.getMessage(), e);
        }
    }
}
