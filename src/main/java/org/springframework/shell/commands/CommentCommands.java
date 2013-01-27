package org.springframework.shell.commands;

import org.springframework.shell.commands.support.DefaultCommentDefinitionImpl;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

/**
 * The following commands do not do anything inherently.  They are merely there to display the commends to the user.
 * Should the developer wish to support comments but hide the "commands", simply do not add this to the Spring Context.
 *
 * Assuming a valid CommentDefinition has been injected to the Shell Component, comment processing will work regardless
 * of the presence of these Commands.
 *
 */
@Component
public class CommentCommands implements CommandMarker {

    @CliCommand(value = { DefaultCommentDefinitionImpl.BLOCK_START }, help = "Start of block comment")
    public void blockCommentBegin() { }

    @CliCommand(value = { DefaultCommentDefinitionImpl.BLOCK_END }, help = "End of block comment")
    public void blockCommentFinish() { }

    @CliCommand(value = { DefaultCommentDefinitionImpl.DOUBLE_SLASH, DefaultCommentDefinitionImpl.SEMICOLON, DefaultCommentDefinitionImpl.POUND },
            help = "Inline comment markers (start of line only)")
    public void inlineComment() { }

}
