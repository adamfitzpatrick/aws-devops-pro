package io.stepinto.codedeployecsdemo;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.stepinto.codedeployecsdemo.service.DogApiService;

@TestConfiguration
public class AppTestConfig {

	@Bean
	public DogApiService dogApiService() {
		return Mockito.mock(DogApiService.class);
	}
}
