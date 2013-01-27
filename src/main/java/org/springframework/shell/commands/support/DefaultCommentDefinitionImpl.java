package org.springframework.shell.commands.support;

import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

public class DefaultCommentDefinitionImpl implements CommentDefinition {

    public static final String DOUBLE_SLASH = "//";
    public static final String SEMICOLON = ";";
    public static final String POUND = "#";
    public static final String BLOCK_START = "/*";
    public static final String BLOCK_END = "*/";

    protected boolean inBlockComment = false;

    @Override
    public String getStartCommentBlock() {
        return BLOCK_START;
    }

    @Override
    public String getEndCommentBlock() {
        return BLOCK_END;
    }

    @Override
    public void blockCommentBegin() {
        Assert.isTrue(!inBlockComment, "Cannot open a new block comment when one already active");
        inBlockComment = true;
    }

    @Override
    public void blockCommentFinish() {
        Assert.isTrue(inBlockComment, "Cannot close a block comment when it has not been opened");
        inBlockComment = false;
    }

    @Override
    public boolean isInBlockComment() {
        return inBlockComment;
    }

    @Override
    public List<String> getLineCommentStarters() {
        return Arrays.asList(new String[] {DOUBLE_SLASH, SEMICOLON, POUND});
    }
}
