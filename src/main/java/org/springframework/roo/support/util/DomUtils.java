package org.springframework.roo.support.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenience methods for working with the DOM API,
 * in particular for working with DOM Nodes and DOM Elements.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Costin Leau
 * @author Alan Stewart
 * @since 1.2.0
 * @see org.w3c.dom.Node
 * @see org.w3c.dom.Element
 */
public final class DomUtils {

	/**
	 * Retrieve all child elements of the given DOM element that match any of
	 * the given element names. Only look at the direct child level of the
	 * given element; do not go into further depth (in contrast to the
	 * DOM API's <code>getElementsByTagName</code> method).
	 *
	 * @param element the DOM element to analyze
	 * @param childElementNames the child element names to look for
	 * @return a List of child <code>org.w3c.dom.Element</code> instances
	 * @see org.w3c.dom.Element
	 * @see org.w3c.dom.Element#getElementsByTagName
	 */
	public static List<Element> getChildElementsByTagName(final Element element, final String[] childElementNames) {
		Assert.notNull(element, "Element must not be null");
		Assert.notNull(childElementNames, "Element names collection must not be null");
		List<String> childEleNameList = Arrays.asList(childElementNames);
		NodeList nl = element.getChildNodes();
		List<Element> childEles = new ArrayList<Element>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameMatch(node, childEleNameList)) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	/**
	 * Retrieve all child elements of the given DOM element that match
	 * the given element name. Only look at the direct child level of the
	 * given element; do not go into further depth (in contrast to the
	 * DOM API's <code>getElementsByTagName</code> method).
	 *
	 * @param element the DOM element to analyze
	 * @param childEleName the child element name to look for
	 * @return a List of child <code>org.w3c.dom.Element</code> instances
	 * @see org.w3c.dom.Element
	 * @see org.w3c.dom.Element#getElementsByTagName
	 */
	public static List<Element> getChildElementsByTagName(final Element element, final String childEleName) {
		return getChildElementsByTagName(element, new String[] {childEleName});
	}

	/**
	 * Returns the first child element identified by its name.
	 *
	 * @param element the DOM element to analyze
	 * @param childElementName the child element name to look for
	 * @return the <code>org.w3c.dom.Element</code> instance,
	 * or <code>null</code> if none found
	 */
	public static Element getChildElementByTagName(final Element element, final String childElementName) {
		Assert.notNull(element, "Element must not be null");
		Assert.notNull(childElementName, "Element name must not be null");
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && nodeNameMatch(node, childElementName)) {
				return (Element) node;
			}
		}
		return null;
	}

	/**
	 * Returns the first child element value identified by its name.
	 *
	 * @param element the DOM element to analyze
	 * @param childElementName the child element name to look for
	 * @return the extracted text value,
	 * or <code>null</code> if no child element found
	 */
	public static String getChildElementValueByTagName(final Element element, final String childElementName) {
		Element child = getChildElementByTagName(element, childElementName);
		return (child != null ? getTextValue(child) : null);
	}

	/**
	 * Extract the text value from the given DOM element, ignoring XML comments.
	 * <p>Appends all CharacterData nodes and EntityReference nodes
	 * into a single String value, excluding Comment nodes.
	 *
	 * @see CharacterData
	 * @see EntityReference
	 * @see Comment
	 */
	public static String getTextValue(final Element valueElement) {
		Assert.notNull(valueElement, "Element must not be null");
		StringBuilder sb = new StringBuilder();
		NodeList nl = valueElement.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node item = nl.item(i);
			if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
				sb.append(item.getNodeValue());
			}
		}
		return sb.toString();
	}

	/**
	 * Namespace-aware equals comparison. Returns <code>true</code> if either
	 * {@link Node#getLocalName} or {@link Node#getNodeName} equals <code>desiredName</code>,
	 * otherwise returns <code>false</code>.
	 *
	 * @param node (required)
	 * @param desiredName (required)
	 * @return
	 */
	public static boolean nodeNameEquals(final Node node, final String desiredName) {
		Assert.notNull(node, "Node must not be null");
		Assert.notNull(desiredName, "Desired name must not be null");
		return nodeNameMatch(node, desiredName);
	}

	/**
	 * Matches the given node's name and local name against the given desired name.
	 *
	 * @param node
	 * @param desiredName
	 * @return
	 */
	private static boolean nodeNameMatch(final Node node, final String desiredName) {
		return (desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName()));
	}

	/**
	 * Matches the given node's name and local name against the given desired names.
	 *
	 * @param node
	 * @param desiredNames
	 * @return
	 */
	private static boolean nodeNameMatch(final Node node, final Collection<?> desiredNames) {
		return (desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName()));
	}

	/**
	 * Removes empty text nodes from the specified node.
	 *
	 * @param node the element where empty text nodes will be removed
	 */
	public static void removeTextNodes(final Node node) {
		if (node == null) {
			return;
		}

		final NodeList children = node.getChildNodes();
		for (int i = children.getLength() - 1; i >= 0; i--) {
			final Node child = children.item(i);
			switch (child.getNodeType()) {
				case Node.ELEMENT_NODE:
					removeTextNodes(child);
					break;
				case Node.CDATA_SECTION_NODE:
				case Node.TEXT_NODE:
					if (StringUtils.isBlank(child.getNodeValue())) {
						node.removeChild(child);
					}
					break;
			}
		}
	}

	/**
	 * Returns the text content of the given {@link Node}, null safe.
	 *
	 * @param node can be <code>null</code>
	 * @param defaultValue the value to return if the node is <code>null</code>
	 * @return the given default value if the node is <code>null</code>
	 * @see Node#getTextContent()
	 * @since 1.2.0
	 */
	public static String getTextContent(final Node node, final String defaultValue) {
		if (node == null) {
			return defaultValue;
		}
		return node.getTextContent();
	}

	/**
	 * Creates a child element with the given name and parent. Avoids the type
	 * of bug whereby the developer calls {@link Document#createElement(String)}
	 * but forgets to append it to the relevant parent.
	 *
	 * @param tagName the name of the new child (required)
	 * @param parent the parent node (required)
	 * @param document the document to which the parent and child belong (required)
	 * @return the created element
	 * @since 1.2.0
	 */
	public static Element createChildElement(final String tagName, final Node parent, final Document document) {
		final Element child = document.createElement(tagName);
		parent.appendChild(child);
		return child;
	}

	/**
	 * Returns the child node with the given tag name, creating it if it does
	 * not exist.
	 *
	 * @param tagName the child tag to look for and possibly create (required)
	 * @param parent the parent in which to look for the child (required)
	 * @param document the document containing the parent (required)
	 * @return the existing or created child (never <code>null</code>)
	 * @since 1.2.0
	 */
	public static Element createChildIfNotExists(final String tagName, final Node parent, final Document document) {
		final Element existingChild = XmlUtils.findFirstElement(tagName, parent);
		if (existingChild != null) {
			return existingChild;
		}
		// No such child; add it
		return createChildElement(tagName, parent, document);
	}

	/**
	 * Returns the text content of the first child of the given parent that has
	 * the given tag name, if any.
	 *
	 * @param parent the parent in which to search (required)
	 * @param child the child name for which to search (required)
	 * @return <code>null</code> if there is no such child, otherwise the first
	 * such child's text content
	 */
	public static String getChildTextContent(final Element parent, final String child) {
		final List<Element> children = XmlUtils.findElements(child, parent);
		if (children.isEmpty()) {
			return null;
		}
		return getTextContent(children.get(0), null);
	}

	/**
	 * Checks in under a given root element whether it can find a child element
	 * which matches the name supplied. Returns {@link Element} if exists.
	 *
	 * @param name the Element name (required)
	 * @param root the parent DOM element (required)
	 * @return the Element if discovered
	 */
	public static Element findFirstElementByName(final String name, final Element root) {
		Assert.hasText(name, "Element name required");
		Assert.notNull(root, "Root element required");
		return (Element) root.getElementsByTagName(name).item(0);
	}
	
	/**
	 * Removes any elements matching the given XPath expression, relative to
	 * the given Element
	 * 
	 * @param xPath the XPath of the element(s) to remove (can be blank)
	 * @param searchBase the element to which the XPath expression is relative
	 */
	public static void removeElements(final String xPath, final Element searchBase) {
		for (final Element elementToDelete : XmlUtils.findElements(xPath, searchBase)) {
			final Node parentNode = elementToDelete.getParentNode();
			parentNode.removeChild(elementToDelete);
			removeTextNodes(parentNode);
		}
	}

	/**
	 * Constructor is private to prevent instantiation
	 */
	private DomUtils() {}
}
