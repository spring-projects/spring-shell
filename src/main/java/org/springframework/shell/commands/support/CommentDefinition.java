package org.springframework.shell.commands.support;

import java.util.List;

public interface CommentDefinition {

    String getStartCommentBlock();
    String getEndCommentBlock();
    public void blockCommentBegin();
    public void blockCommentFinish();
    boolean isInBlockComment();
    List<String> getLineCommentStarters();

}
