package io.stepinto.codepipelinedemo.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class AllBreedsDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	private HashMap<String, String[]> message;
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		AllBreedsDTO other = (AllBreedsDTO) o;
		return message.entrySet().stream()
				.allMatch(e -> Arrays.equals(e.getValue(), other.getMessage().get(e.getKey())));
	}
}
