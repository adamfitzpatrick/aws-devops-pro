package io.stepinto.codepipelinedemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.stepinto.codepipelinedemo.dto.AllBreedsDTO;

@Service
public class DogApiService {
	
	protected static final String ALL_URI = "https://dog.ceo/api/breeds/list/all";
	
	@Autowired
	private RestTemplate restTemplate;
	
	public AllBreedsDTO getAllBreeds() {
		return restTemplate.getForObject(ALL_URI, AllBreedsDTO.class);
	}
}
