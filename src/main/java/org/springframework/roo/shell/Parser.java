package org.springframework.roo.shell;

import java.util.List;

/**
 * Interface for {@link SimpleParser}.
 *
 * @author Ben Alex
 * @author Alan Stewart
 * @since 1.0
 */
public interface Parser {

	ParseResult parse(String buffer);

	/**
	 * Populates a list of completion candidates. This method is required for backward compatibility for STS versions up to 2.8.0.
	 * 
	 * @param buffer
	 * @param cursor
	 * @param candidates
	 * @return
	 */
	int complete(String buffer, int cursor, List<String> candidates);

	/**
	 * Populates a list of completion candidates. 
	 * 
	 * @param buffer
	 * @param cursor
	 * @param candidates
	 * @return
	 */
	int completeAdvanced(String buffer, int cursor, List<Completion> candidates);
}