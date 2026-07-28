package com.vitaltrip.vitaltrip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import support.TestcontainersConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class VitaltripApplicationTests {

	@Test
	void contextLoads() {
	}

}
