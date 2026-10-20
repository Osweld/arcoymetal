package com.arcoymetal.arcobackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ArcobackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
