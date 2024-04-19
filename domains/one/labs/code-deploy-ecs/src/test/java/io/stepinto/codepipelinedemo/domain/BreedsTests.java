package io.stepinto.codepipelinedemo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.stepinto.codepipelinedemo.dto.AllBreedsDTO;

@SpringBootTest
public class BreedsTests {

	private Breeds sut;
	
	@BeforeEach
	public void setup() {
		HashMap<String, String[]> message = new HashMap<>();
		message.put("BreedA", new String[] {"Foo", "Bar"});
		message.put("BreedB", new String[] {});
		AllBreedsDTO dto = new AllBreedsDTO();
		dto.setMessage(message);
		sut = new Breeds(dto);
	}
	
	@Test
	public void listAllBreedsTest() {
		Set<String> expected = Set.of("BreedA", "BreedB");
		assertEquals(expected, sut.listAllBreeds());
	}
	
	@Test
	public void listAllSubBreedsTest() {
		Set<String> expected = Set.of("Foo BreedA", "Bar BreedA", "BreedB");
		assertEquals(expected, sut.listAllSubBreeds());
	}
	
	@Test
	public void getSubBreedsTest() {
		assertEquals(Set.of("Foo", "Bar"), sut.getSubBreeds("BreedA"));
		assertEquals(Collections.emptySet(), sut.getSubBreeds("BreedB"));
	}
	
	@Test
	public void getSubBreedsInvalidBreedTest() {
		assertNull(sut.getSubBreeds("Invalid"));
	}
}
