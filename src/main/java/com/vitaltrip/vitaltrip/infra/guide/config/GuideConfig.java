package com.vitaltrip.vitaltrip.infra.guide.config;

import com.vitaltrip.vitaltrip.domain.first_aid.guide.GuideRegistry;
import com.vitaltrip.vitaltrip.infra.guide.GuideYamlLoader;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class GuideConfig {

    @Bean
    public GuideRegistry guideRegistry(GuideProperties properties) throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(properties.location());
        return new GuideRegistry(GuideYamlLoader.create().load(resources));
    }
}
