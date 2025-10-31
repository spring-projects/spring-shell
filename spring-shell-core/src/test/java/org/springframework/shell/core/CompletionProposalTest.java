/*
 * Copyright 2018-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletionProposalTest {

    private final String PROPOSAL = "test proposal";
    private final String DESCRIPTION = "description";
    private final String DISPLAY_TEXT = "displayText";
    private final boolean DONT_QUOTE = true;
    private final String CATEGORY = "category";
    CompletionProposal completionProposal;


    @BeforeEach
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

        assertEquals(completionProposal, completionProposal2);
    }

    @Test
    public void hashcode() {
        CompletionProposal completionProposal2 = new CompletionProposal(PROPOSAL);
        completionProposal2.category(CATEGORY);
        completionProposal2.description(DESCRIPTION);
        completionProposal2.displayText(DISPLAY_TEXT);
        completionProposal2.dontQuote(DONT_QUOTE);
        assertEquals(completionProposal.hashCode(), completionProposal2.hashCode());
    }
}