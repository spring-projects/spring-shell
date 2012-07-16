/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.shell.support.api;

import java.util.logging.Logger;

/**
 * Interface defining an add-on search service.
 *
 * <p>
 * This interface is included in the support module because several of Roo's core
 * infrastructure modules require add-on search capabilities.
 *
 * @author Ben Alex
 * @author Stefan Schmidt
 * @since 1.1.1
 */
public interface AddOnSearch {

	/**
	 * Search all add-ons presently known this Roo instance, including add-ons which have
	 * not been downloaded or installed by the user.
	 *
	 * <p>
	 * Information is optionally emitted to the console via {@link Logger#info}.
	 *
	 * @param showFeedback if false will never output any messages to the console (required)
	 * @param searchTerms comma separated list of search terms (required)
	 * @param refresh attempt a fresh download of roobot.xml (optional)
	 * @param linesPerResult maximum number of lines per add-on (optional)
	 * @param maxResults maximum number of results to display (optional)
	 * @param trustedOnly display only trusted add-ons in search results (optional)
	 * @param compatibleOnly display only compatible add-ons in search results (optional)
	 * @param communityOnly display only community-provided add-ons in search results (optional)
	 * @param requiresCommand display only add-ons which offer the specified command (optional)
	 * @return the total number of matches found, even if only some of these are displayed due to maxResults
	 * (or null if the add-on list is unavailable for some reason, eg network problems etc)
	 */
	Integer searchAddOns(boolean showFeedback, String searchTerms, boolean refresh, int linesPerResult, int maxResults, boolean trustedOnly, boolean compatibleOnly, boolean communityOnly, String requiresCommand);
}
