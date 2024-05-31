package io.stepinto.codedeployecsdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.stepinto.codedeployecsdemo.domain.Breeds;
import io.stepinto.codedeployecsdemo.service.DogApiService;

@RestController
@RequestMapping("match")
public class DogMatchController {

	@Autowired
	private DogApiService dogApiService;
	
	@GetMapping("/length/{word}")
	public String matchLength(@PathVariable String word) {
		Breeds breeds = new Breeds(dogApiService.getAllBreeds());
		return breeds.listAllSubBreeds().stream()
				.filter(dog -> dog.length() == word.length())
				.findFirst().orElse("Sorry; I couldn't find the dog for you!");
	}
}
