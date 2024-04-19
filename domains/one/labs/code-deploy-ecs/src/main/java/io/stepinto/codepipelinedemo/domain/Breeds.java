package io.stepinto.codepipelinedemo.domain;

import java.util.function.Function;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.stepinto.codepipelinedemo.dto.AllBreedsDTO;

public class Breeds {

	private final HashMap<String, Set<String>> breeds;
	
	public Breeds(AllBreedsDTO allBreeds) {
		breeds = allBreeds.getMessage().entrySet().stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),
						e -> Set.of(e.getValue()),
						(prev, next) -> next,
						HashMap::new));
	}
	
	public Set<String> listAllBreeds() {
		return breeds.keySet();
	}
	
	public Set<String> listAllSubBreeds() {
		Function<Map.Entry<String, Set<String>>, Stream<String>> mapper = entry -> {
			if (entry.getValue().isEmpty()) {
				return Stream.of(entry.getKey());
			}
			return entry.getValue().stream().map(v -> v + " " + entry.getKey());
		};
		return breeds.entrySet().stream()
				.flatMap(mapper)
				.collect(Collectors.toSet());
	}
	
	public Set<String> getSubBreeds(String breed) {
		return breeds.get(breed);
	}
}
