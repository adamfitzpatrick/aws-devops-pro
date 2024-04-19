package io.stepinto.codepipelinedemo.controller
;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.containsString;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.stepinto.codepipelinedemo.AppTestConfig;
import io.stepinto.codepipelinedemo.dto.AllBreedsDTO;
import io.stepinto.codepipelinedemo.service.DogApiService;

@Import(AppTestConfig.class)
@SpringBootTest
public class DogMatchControllerTests {

	@Autowired
	private DogMatchController sut;
	
	@Autowired
	private DogApiService dogApiService;
	
	@BeforeEach
	public void setup() {
		HashMap<String, String[]> message = new HashMap<>();
		message.put("Alpha", new String[] {"Foo", "Bar"});
		message.put("Beta", new String[] {});
		AllBreedsDTO allBreedsDTO = new AllBreedsDTO();
		allBreedsDTO.setMessage(message);
		Mockito.when(dogApiService.getAllBreeds()).thenReturn(allBreedsDTO);
	}
	
	@Test
	public void matchLengthTest() {
		assertEquals("Foo Alpha", sut.matchLength("Ben Bravo"));
		assertEquals("Beta", sut.matchLength("John"));
	}
	
	@Test
	public void matchLengthNoMatchTest() {
		assertThat(sut.matchLength("Abe"), containsString("Sorry"));
	}
}
