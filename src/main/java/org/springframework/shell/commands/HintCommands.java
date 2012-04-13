package org.springframework.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.stereotype.Component;

@Component
public class HintCommands implements CommandMarker {

	// Fields
	@Autowired private HintOperations hintOperations;

	//TODO: figure out how to provide hint
	//@CliCommand(value = "hint", help = "Provides step-by-step hints and context-sensitive guidance")
	public String hint(
		@CliOption(key = { "topic", "" }, mandatory = false, unspecifiedDefaultValue = "", optionContext = "disable-string-converter,topics", help = "The topic for which advice should be provided") final String topic) {

		return hintOperations.hint(topic);
	}
}
