package org.springframework.shell;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CompletionProposalTest {

    private final String PROPOSAL = "test proposal";
    private final String DESCRIPTION = "description";
    private final String DISPLAY_TEXT = "displayText";
    private final boolean DONT_QUOTE = true;
    private final String CATEGORY = "category";
    CompletionProposal completionProposal;


    @Before
    public void setup() {
        completionProposal = new CompletionProposal(PROPOSAL);
        completionProposal.category(CATEGORY);
        completionProposal.description(DESCRIPTION);
        completionProposal.displayText(DISPLAY_TEXT);
        completionProposal.dontQuote(DONT_QUOTE);
    }

    @Test
    public void equals() {
        CompletionProposal completionProposal2 = new CompletionProposal(PROPOSAL);
        completionProposal2.category(CATEGORY);
        completionProposal2.description(DESCRIPTION);
        completionProposal2.displayText(DISPLAY_TEXT);
        completionProposal2.dontQuote(DONT_QUOTE);

        assertThat(completionProposal, is(completionProposal2));
    }

    @Test
    public void hashcode() {
        CompletionProposal completionProposal2 = new CompletionProposal(PROPOSAL);
        completionProposal2.category(CATEGORY);
        completionProposal2.description(DESCRIPTION);
        completionProposal2.displayText(DISPLAY_TEXT);
        completionProposal2.dontQuote(DONT_QUOTE);
        assertThat(completionProposal.hashCode(), is(completionProposal2.hashCode()));
    }
}