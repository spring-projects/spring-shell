package org.springframework.shell2.jcommander;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * Created by ericbottard on 15/12/15.
 */
public class FieldCollins {

	@Parameter(names = "--name")
	private String name;

	@Parameter(names = "-level")
	private int level;

	@Parameter(description = "rest")
	private List<String> rest = new ArrayList<>();

	public List<String> getRest() {
		return rest;
	}

	public void setRest(List<String> rest) {
		this.rest = rest;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
