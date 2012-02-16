package org.springframework.shell.commands;

import java.util.SortedSet;

public interface HintOperations {

	String hint(String topic);

	SortedSet<String> getCurrentTopics();
}
