package org.springframework.shell.docs;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

public class AnnotationRegistrationSnippets {

	// tag::snippet1[]
	@ShellComponent
	static class MyCommands {

		@ShellMethod
		public void mycommand() {
		}
	}
	// end::snippet1[]

	static class Dump1 {

		// tag::snippet2[]
		@ShellMethod(value = "Add numbers.", key = "sum")
		public int add(int a, int b) {
			return a + b;
		}
		// end::snippet2[]
	}
}
