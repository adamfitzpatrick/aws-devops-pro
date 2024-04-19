package io.stepinto.codepipelinedemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.stepinto.codepipelinedemo.dto.AllBreedsDTO;

@SpringBootTest
public class DogApiServiceTest {

	@Autowired
	private DogApiService sut;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private MockRestServiceServer mockServer;
	private ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	public void setup() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}
	
	@Test
	public void getAllBreedsTest() throws URISyntaxException, JsonProcessingException {
		HashMap<String, String[]> message = new HashMap<>();
		message.put("BreedA", new String[] {"Foo", "Bar"});
		message.put("BreedB", new String[] {});
		AllBreedsDTO expectedBreeds = new AllBreedsDTO();
		expectedBreeds.setMessage(message);
		mockServer.expect(ExpectedCount.once(),
				requestTo(new URI(DogApiService.ALL_URI)))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body(mapper.writeValueAsString(expectedBreeds)));
		
		AllBreedsDTO actualBreeds = sut.getAllBreeds();
		
		mockServer.verify();
		assertEquals(expectedBreeds, actualBreeds);
	}
}
