package org.springframework.roo.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helper util class to allow more convenient handling of web.xml file in Web projects.
 *
 * @author Stefan Schmidt
 * @since 1.1
 */
public final class WebXmlUtils {

	// Constants
	private static final String WEB_APP_XPATH = "/web-app/";
	private static final String WHITESPACE = "[ \t\r\n]";

	/**
	 * Set the display-name element in the web.xml document.
	 * 
	 * @param displayName (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 */
	public static void setDisplayName(final String displayName, final Document document, final String comment) {
		Assert.hasText(displayName, "display name required");
		Assert.notNull(document, "Web XML document required");

		Element displayNameElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "display-name", document.getDocumentElement());
		if (displayNameElement == null) {
			displayNameElement = document.createElement("display-name");
			insertBetween(displayNameElement, "the-start", "description", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(displayNameElement, comment, document);
			}
		}
		displayNameElement.setTextContent(displayName);
	}

	/**
	 * Set the description element in the web.xml document.
	 * 
	 * @param description (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 */
	public static void setDescription(final String description, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText(description, "Description required");

		Element descriptionElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "description", document.getDocumentElement());
		if (descriptionElement == null) {
			descriptionElement = document.createElement("description");
			insertBetween(descriptionElement, "display-name[last()]", "context-param", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(descriptionElement, comment, document);
			}
		}
		descriptionElement.setTextContent(description);
	}

	/**
	 * Add a context param to the web.xml document
	 * 
	 * @param contextParam (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 */
	public static void addContextParam(final WebXmlParam contextParam, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.notNull(contextParam, "Context param required");

		Element contextParamElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "context-param[param-name = '" + contextParam.getName() + "']", document.getDocumentElement());
		if (contextParamElement == null) {
			contextParamElement = new XmlElementBuilder("context-param", document).addChild(new XmlElementBuilder("param-name", document).setText(contextParam.getName()).build()).build();
			insertBetween(contextParamElement, "description[last()]", "filter", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(contextParamElement, comment, document);
			}
		}
		appendChildIfNotPresent(contextParamElement, new XmlElementBuilder("param-value", document).setText(contextParam.getValue()).build());
	}

	/**
	 * Add a new filter definition to web.xml document. The filter will be added AFTER (FilterPosition.LAST) all existing filters.
	 * 
	 * @param filterName (required)
	 * @param filterClass the fully qualified name of the filter type (required)
	 * @param urlPattern (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 * @param initParams a vararg of initial parameters (optional)
	 */
	public static void addFilter(final String filterName, final String filterClass, final String urlPattern, final Document document, final String comment, final WebXmlParam... initParams) {
		addFilterAtPosition(FilterPosition.LAST, null, null, filterName, filterClass, urlPattern, document, comment, initParams);
	}

	/**
	 * Add a new filter definition to web.xml document. The filter will be added at the FilterPosition specified.
	 * 
	 * @param filterPosition Filter position (required)
	 * @param beforeFilterName (optional for filter position FIRST and LAST, required for BEFORE and AFTER)
	 * @param filterName (required)
	 * @param filterClass the fully qualified name of the filter type (required)
	 * @param urlPattern (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 * @param initParams (optional)
	 */
	public static void addFilterAtPosition(final FilterPosition filterPosition, final String afterFilterName, final String beforeFilterName, final String filterName, final String filterClass, final String urlPattern, final Document document, final String comment, final WebXmlParam... initParams) {
		addFilterAtPosition(filterPosition, afterFilterName, beforeFilterName, filterName, filterClass, urlPattern, document, comment, initParams == null ? new ArrayList<WebXmlParam>() : Arrays.asList(initParams), new ArrayList<Dispatcher>());
	}

	/**
	 * Add a new filter definition to web.xml document. The filter will be added at the FilterPosition specified.
	 * 
	 * @param filterPosition Filter position (required)
	 * @param beforeFilterName (optional for filter position FIRST and LAST, required for BEFORE and AFTER)
	 * @param filterName (required)
	 * @param filterClass the fully qualified name of the filter type (required)
	 * @param urlPattern (required)
	 * @param document the web.xml document (required)
	 * @param comment (optional)
	 * @param initParams (optional)
	 * @param dispatchers (optional)
	 */
	public static void addFilterAtPosition(final FilterPosition filterPosition, final String afterFilterName, final String beforeFilterName, final String filterName, final String filterClass, final String urlPattern, final Document document, final String comment, List<WebXmlParam> initParams, final List<Dispatcher> dispatchers) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText(filterName, "Filter name required");
		Assert.hasText(filterClass, "Filter class required");
		Assert.notNull(urlPattern, "Filter URL mapping pattern required");

		if (initParams == null) {
			initParams = new ArrayList<WebXmlUtils.WebXmlParam>();
		}

		// Creating filter
		Element filterElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "filter[filter-name = '" + filterName + "']", document.getDocumentElement());
		if (filterElement == null) {
			filterElement = new XmlElementBuilder("filter", document).addChild(new XmlElementBuilder("filter-name", document).setText(filterName).build()).build();
			if (filterPosition.equals(FilterPosition.FIRST)) {
				insertBetween(filterElement, "context-param", "filter", document);
			} else if (filterPosition.equals(FilterPosition.BEFORE)) {
				Assert.hasText(beforeFilterName, "The filter position filter name is required when using FilterPosition.BEFORE");
				insertBefore(filterElement, "filter[filter-name = '" + beforeFilterName + "']", document);
			} else if (filterPosition.equals(FilterPosition.AFTER)) {
				Assert.hasText(afterFilterName, "The filter position filter name is required when using FilterPosition.AFTER");
				insertAfter(filterElement, "filter[filter-name = '" + afterFilterName + "']", document);
			} else if (filterPosition.equals(FilterPosition.BETWEEN)) {
				Assert.hasText(beforeFilterName, "The 'before' filter name is required when using FilterPosition.BETWEEN");
				Assert.hasText(afterFilterName, "The 'after' filter name is required when using FilterPosition.BETWEEN");
				insertBetween(filterElement, "filter[filter-name = '" + afterFilterName + "']", "filter[filter-name = '" + beforeFilterName + "']", document);
			} else {
				insertBetween(filterElement, "context-param[last()]", "filter-mapping", document);
			}
			if (StringUtils.hasText(comment)) {
				addCommentBefore(filterElement, comment, document);
			}
		}
		appendChildIfNotPresent(filterElement, new XmlElementBuilder("filter-class", document).setText(filterClass).build());
		for (final WebXmlParam initParam : initParams) {
			appendChildIfNotPresent(filterElement, new XmlElementBuilder("init-param", document).addChild(new XmlElementBuilder("param-name", document).setText(initParam.getName()).build()).addChild(new XmlElementBuilder("param-value", document).setText(initParam.getValue()).build()).build());
		}

		// Creating filter mapping
		Element filterMappingElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "filter-mapping[filter-name = '" + filterName + "']", document.getDocumentElement());
		if (filterMappingElement == null) {
			filterMappingElement = new XmlElementBuilder("filter-mapping", document).addChild(new XmlElementBuilder("filter-name", document).setText(filterName).build()).build();
			if (filterPosition.equals(FilterPosition.FIRST)) {
				insertBetween(filterMappingElement, "filter", "filter-mapping", document);
			} else if (filterPosition.equals(FilterPosition.BEFORE)) {
				insertBefore(filterMappingElement, "filter-mapping[filter-name = '" + beforeFilterName + "']", document);
			} else if (filterPosition.equals(FilterPosition.AFTER)) {
				insertAfter(filterMappingElement, "filter-mapping[filter-name = '" + beforeFilterName + "']", document);
			} else if (filterPosition.equals(FilterPosition.BETWEEN)) {
				insertBetween(filterMappingElement, "filter-mapping[filter-name = '" + afterFilterName + "']", "filter-mapping[filter-name = '" + beforeFilterName + "']", document);
			} else {
				insertBetween(filterMappingElement, "filter-mapping[last()]", "listener", document);
			}
		}
		appendChildIfNotPresent(filterMappingElement, new XmlElementBuilder("url-pattern", document).setText(urlPattern).build());
		for (final Dispatcher dispatcher : dispatchers) {
			appendChildIfNotPresent(filterMappingElement, new XmlElementBuilder("dispatcher", document).setText(dispatcher.name()).build());
		}
	}

	/**
	 * Add listener element to web.xml document
	 * 
	 * @param className the fully qualified name of the listener type (required)
	 * @param document (required)
	 * @param comment (optional)
	 */
	public static void addListener(final String className, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText(className, "Class name required");

		Element listenerElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "listener[listener-class = '" + className + "']", document.getDocumentElement());
		if (listenerElement == null) {
			listenerElement = new XmlElementBuilder("listener", document).addChild(new XmlElementBuilder("listener-class", document).setText(className).build()).build();
			insertBetween(listenerElement, "filter-mapping[last()]", "servlet", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(listenerElement, comment, document);
			}
		}
	}

	/**
	 * Add servlet element to the web.xml document
	 * 
	 * @param servletName (required)
	 * @param className the fully qualified name of the servlet type (required)
	 * @param urlPattern this can be set to null in which case the servletName will be used for mapping (optional)
	 * @param loadOnStartup (optional)
	 * @param document (required)
	 * @param comment (optional)
	 * @param initParams (optional)
	 */
	public static void addServlet(final String servletName, final String className, final String urlPattern, final Integer loadOnStartup, final Document document, final String comment, final WebXmlParam... initParams) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText(servletName, "Servlet name required");
		Assert.hasText(className, "Fully qualified class name required");

		// Create servlet
		Element servletElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "servlet[servlet-name = '" + servletName + "']", document.getDocumentElement());
		if (servletElement == null) {
			servletElement = new XmlElementBuilder("servlet", document).addChild(new XmlElementBuilder("servlet-name", document).setText(servletName).build()).build();
			insertBetween(servletElement, "listener[last()]", "servlet-mapping", document);
			if (comment != null && comment.length() > 0) {
				addCommentBefore(servletElement, comment, document);
			}
		}
		appendChildIfNotPresent(servletElement, new XmlElementBuilder("servlet-class", document).setText(className).build());
		for (final WebXmlParam initParam : initParams) {
			appendChildIfNotPresent(servletElement, new XmlElementBuilder("init-param", document).addChild(new XmlElementBuilder("param-name", document).setText(initParam.getName()).build()).addChild(new XmlElementBuilder("param-value", document).setText(initParam.getValue()).build()).build());
		}
		if (loadOnStartup != null) {
			appendChildIfNotPresent(servletElement, new XmlElementBuilder("load-on-startup", document).setText(loadOnStartup.toString()).build());
		}

		// Create servlet mapping
		Element servletMappingElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "servlet-mapping[servlet-name = '" + servletName + "']", document.getDocumentElement());
		if (servletMappingElement == null) {
			servletMappingElement = new XmlElementBuilder("servlet-mapping", document).addChild(new XmlElementBuilder("servlet-name", document).setText(servletName).build()).build();
			insertBetween(servletMappingElement, "servlet[last()]", "session-config", document);
		}
		if (StringUtils.hasText(urlPattern)) {
			appendChildIfNotPresent(servletMappingElement, new XmlElementBuilder("url-pattern", document).setText(urlPattern).build());
		} else {
			appendChildIfNotPresent(servletMappingElement, new XmlElementBuilder("servlet-name", document).setText(servletName).build());
		}
	}

	/**
	 * Set session timeout in web.xml document
	 * 
	 * @param timeout
	 * @param document (required)
	 * @param comment (optional)
	 */
	public static void setSessionTimeout(final int timeout, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.notNull(timeout, "Timeout required");

		Element sessionConfigElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "session-config", document.getDocumentElement());
		if (sessionConfigElement == null) {
			sessionConfigElement = document.createElement("session-config");
			insertBetween(sessionConfigElement, "servlet-mapping[last()]", "welcome-file-list", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(sessionConfigElement, comment, document);
			}
		}
		appendChildIfNotPresent(sessionConfigElement, new XmlElementBuilder("session-timeout", document).setText(String.valueOf(timeout)).build());
	}

	/**
	 * Add a welcome file definition to web.xml document
	 * 
	 * @param path (required)
	 * @param document (required)
	 * @param comment (optional)
	 */
	public static void addWelcomeFile(final String path, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText("Path required");

		Element welcomeFileElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "welcome-file-list", document.getDocumentElement());
		if (welcomeFileElement == null) {
			welcomeFileElement = document.createElement("welcome-file-list");
			insertBetween(welcomeFileElement, "session-config[last()]", "error-page", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(welcomeFileElement, comment, document);
			}
		}
		appendChildIfNotPresent(welcomeFileElement, new XmlElementBuilder("welcome-file", document).setText(path).build());
	}

	/**
	 * Add exception type to web.xml document
	 * 
	 * @param exceptionType fully qualified exception type name (required)
	 * @param location (required)
	 * @param document (required)
	 * @param comment (optional)
	 */
	public static void addExceptionType(final String exceptionType, final String location, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.hasText(exceptionType, "Fully qualified exception type name required");
		Assert.hasText(location, "location required");

		Element errorPageElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "error-page[exception-type = '" + exceptionType + "']", document.getDocumentElement());
		if (errorPageElement == null) {
			errorPageElement = new XmlElementBuilder("error-page", document).addChild(new XmlElementBuilder("exception-type", document).setText(exceptionType).build()).build();
			insertBetween(errorPageElement, "welcome-file-list[last()]", "the-end", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(errorPageElement, comment, document);
			}
		}
		appendChildIfNotPresent(errorPageElement, new XmlElementBuilder("location", document).setText(location).build());
	}

	/**
	 * Add error code to web.xml document
	 * 
	 * @param errorCode (required)
	 * @param location (required)
	 * @param document (required)
	 * @param comment (optional)
	 */
	public static void addErrorCode(final Integer errorCode, final String location, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.notNull(errorCode, "Error code required");
		Assert.hasText(location, "Location required");

		Element errorPageElement = XmlUtils.findFirstElement(WEB_APP_XPATH + "error-page[error-code = '" + errorCode.toString() + "']", document.getDocumentElement());
		if (errorPageElement == null) {
			errorPageElement = new XmlElementBuilder("error-page", document).addChild(new XmlElementBuilder("error-code", document).setText(errorCode.toString()).build()).build();
			insertBetween(errorPageElement, "welcome-file-list[last()]", "the-end", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(errorPageElement, comment, document);
			}
		}
		appendChildIfNotPresent(errorPageElement, new XmlElementBuilder("location", document).setText(location).build());
	}

	/**
	 * Add a security constraint to a web.xml document
	 * 
	 * @param displayName (optional)
	 * @param webResourceCollections (required)
	 * @param roleNames (optional)
	 * @param transportGuarantee (optional)
	 * @param document (required)
	 * @param comment (optional)
	 * */
	public static void addSecurityConstraint(final String displayName, final List<WebResourceCollection> webResourceCollections, final List<String> roleNames, final String transportGuarantee, final Document document, final String comment) {
		Assert.notNull(document, "Web XML document required");
		Assert.isTrue(!CollectionUtils.isEmpty(webResourceCollections), "A security-constraint element must contain at least one web-resource-collection");

		Element securityConstraintElement = XmlUtils.findFirstElement("security-constraint", document.getDocumentElement());
		if (securityConstraintElement == null) {
			securityConstraintElement = document.createElement("security-constraint");
			insertAfter(securityConstraintElement, "session-config[last()]", document);
			if (StringUtils.hasText(comment)) {
				addCommentBefore(securityConstraintElement, comment, document);
			}
		}

		if (StringUtils.hasText(displayName)) {
			appendChildIfNotPresent(securityConstraintElement, new XmlElementBuilder("display-name", document).setText(displayName).build());
		}

		for (final WebResourceCollection webResourceCollection : webResourceCollections) {
			final XmlElementBuilder webResourceCollectionBuilder = new XmlElementBuilder("web-resource-collection", document);
			Assert.hasText(webResourceCollection.getWebResourceName(), "web-resource-name is required");
			webResourceCollectionBuilder.addChild(new XmlElementBuilder("web-resource-name", document).setText(webResourceCollection.getWebResourceName()).build());
			if (StringUtils.hasText(webResourceCollection.getDescription())) {
				webResourceCollectionBuilder.addChild(new XmlElementBuilder("description", document).setText(webResourceCollection.getWebResourceName()).build());
			}
			for (final String urlPattern : webResourceCollection.getUrlPatterns()) {
				if (StringUtils.hasText(urlPattern)) {
					webResourceCollectionBuilder.addChild(new XmlElementBuilder("url-pattern", document).setText(urlPattern).build());
				}
			}
			for (final String httpMethod : webResourceCollection.getHttpMethods()) {
				if (StringUtils.hasText(httpMethod)) {
					webResourceCollectionBuilder.addChild(new XmlElementBuilder("http-method", document).setText(httpMethod).build());
				}
			}
			appendChildIfNotPresent(securityConstraintElement, webResourceCollectionBuilder.build());
		}

		if (roleNames != null && roleNames.size() > 0) {
			final XmlElementBuilder authConstraintBuilder = new XmlElementBuilder("auth-constraint", document);
			for (final String roleName : roleNames) {
				if (StringUtils.hasText(roleName)) {
					authConstraintBuilder.addChild(new XmlElementBuilder("role-name", document).setText(roleName).build());
				}
			}
			appendChildIfNotPresent(securityConstraintElement, authConstraintBuilder.build());
		}

		if (StringUtils.hasText(transportGuarantee)) {
			final XmlElementBuilder userDataConstraintBuilder = new XmlElementBuilder("user-data-constraint", document);
			userDataConstraintBuilder.addChild(new XmlElementBuilder("transport-guarantee", document).setText(transportGuarantee).build());
			appendChildIfNotPresent(securityConstraintElement, userDataConstraintBuilder.build());
		}
	}

	private static void insertBetween(final Element element, final String afterElementName, final String beforeElementName, final Document document) {
		final Element beforeElement = XmlUtils.findFirstElement(WEB_APP_XPATH + beforeElementName, document.getDocumentElement());
		if (beforeElement != null) {
			document.getDocumentElement().insertBefore(element, beforeElement);
			addLineBreakBefore(element, document);
			addLineBreakBefore(element, document);
			return;
		}

		final Element afterElement = XmlUtils.findFirstElement(WEB_APP_XPATH + afterElementName, document.getDocumentElement());
		if (afterElement != null && afterElement.getNextSibling() != null && afterElement.getNextSibling() instanceof Element) {
			document.getDocumentElement().insertBefore(element, afterElement.getNextSibling());
			addLineBreakBefore(element, document);
			addLineBreakBefore(element, document);
			return;
		}

		document.getDocumentElement().appendChild(element);
		addLineBreakBefore(element, document);
		addLineBreakBefore(element, document);
	}

	private static void insertBefore(final Element element, final String beforeElementName, final Document document) {
		final Element beforeElement = XmlUtils.findFirstElement(WEB_APP_XPATH + beforeElementName, document.getDocumentElement());
		if (beforeElement != null) {
			document.getDocumentElement().insertBefore(element, beforeElement);
			addLineBreakBefore(element, document);
			addLineBreakBefore(element, document);
			return;
		}
		document.getDocumentElement().appendChild(element);
		addLineBreakBefore(element, document);
		addLineBreakBefore(element, document);
	}

	private static void insertAfter(final Element element, final String afterElementName, final Document document) {
		final Element afterElement = XmlUtils.findFirstElement(WEB_APP_XPATH + afterElementName, document.getDocumentElement());
		if (afterElement != null && afterElement.getNextSibling() != null && afterElement.getNextSibling() instanceof Element) {
			document.getDocumentElement().insertBefore(element, afterElement.getNextSibling());
			addLineBreakBefore(element, document);
			addLineBreakBefore(element, document);
			return;
		}
		document.getDocumentElement().appendChild(element);
		addLineBreakBefore(element, document);
		addLineBreakBefore(element, document);
	}

	/**
	 * Adds the given child to the given parent if it's not already there
	 * 
	 * @param parent the parent to which to add a child (required)
	 * @param child the child to add if not present (required)
	 */
	private static void appendChildIfNotPresent(final Node parent, final Element child) {
		final NodeList existingChildren = parent.getChildNodes();
		for (int i = 0; i < existingChildren.getLength(); i++) {
			final Node existingChild = existingChildren.item(i);
			if (existingChild instanceof Element) {
				// Attempt matching of possibly nested structures by using of 'getTextContent' as 'isEqualNode' does not match due to line returns, etc
				// Note, this does not work if child nodes are appearing in a different order than expected
				if (existingChild.getNodeName().equals(child.getNodeName()) && existingChild.getTextContent().replaceAll(WHITESPACE, "").trim().equals(child.getTextContent().replaceAll(WHITESPACE, ""))) {
					// If we found a match, there is no need to append the child element
					return;
				}
			}
		}
		parent.appendChild(child);
	}

	private static void addLineBreakBefore(final Element element, final Document document) {
		document.getDocumentElement().insertBefore(document.createTextNode("\n    "), element);
	}

	private static void addCommentBefore(final Element element, final String comment, final Document document) {
		if (null == XmlUtils.findNode("//comment()[.=' " + comment + " ']", document.getDocumentElement())) {
			document.getDocumentElement().insertBefore(document.createComment(" " + comment + " "), element);
			addLineBreakBefore(element, document);
		}
	}

	/**
	 * Value object that holds init-param style information
	 * 
	 * @author Stefan Schmidt
	 * @since 1.1
	 */
	public static class WebXmlParam extends Pair<String, String> {

		/**
		 * Constructor
		 * 
		 * @param name
		 * @param value
		 */
		public WebXmlParam(final String name, final String value) {
			super(name, value);
		}

		/**
		 * Returns the name of this parameter
		 * 
		 * @return
		 */
		public String getName() {
			return getKey();
		}
	}

	/**
	 * Enum to define filter position
	 * 
	 * @author Stefan Schmidt
	 * @since 1.1
	 * 
	 */
	public static enum FilterPosition {
		FIRST, LAST, BEFORE, AFTER, BETWEEN;
	}

	/**
	 * Enum to define dispatcher
	 * 
	 * @author Stefan Schmidt
	 * @since 1.1.1
	 * 
	 */
	public static enum Dispatcher {
		FORWARD, REQUEST, INCLUDE, ERROR;
	}

	/**
	 * Convenience class for passing a web-resource-collection element's details
	 * 
	 * @since 1.1.1
	 */
	public static class WebResourceCollection {
		private final String webResourceName;
		private final String description;
		private final List<String> urlPatterns;
		private final List<String> httpMethods;

		public WebResourceCollection(final String webResourceName, final String description, final List<String> urlPatterns, final List<String> httpMethods) {
			this.webResourceName = webResourceName;
			this.description = description;
			this.urlPatterns = urlPatterns;
			this.httpMethods = httpMethods;
		}

		public String getWebResourceName() {
			return webResourceName;
		}

		public List<String> getUrlPatterns() {
			return urlPatterns;
		}

		public List<String> getHttpMethods() {
			return httpMethods;
		}

		public String getDescription() {
			return description;
		}
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private WebXmlUtils() {
	}
}
