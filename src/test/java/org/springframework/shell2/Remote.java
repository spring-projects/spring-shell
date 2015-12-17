package org.springframework.shell2;

/**
 * An example commands class.
 *
 * @author Eric Bottard
 * @author Florent Biville
 */
public class Remote {

	/**
	 * A command method that showcases<ul>
	 *     <li>default handling for booleans (force)</li>
	 *     <li>default parameter name discovery (name)</li>
	 *     <li>default value supplying (foo and bar)</li>
	 * </ul>
	 */
	@ShellMethod
	public void zap(boolean force,
	                String name,
	                @ShellOption(defaultValue="defoolt") String foo,
	                @ShellOption(value = {"bar", "baz"}, defaultValue = "last") String bar) {

	}
}
