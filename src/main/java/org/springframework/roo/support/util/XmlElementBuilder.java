package org.springframework.roo.support.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Very simple convenience Builder for XML {@code Element}s
 *
 * @author Stefan Schmidt
 * @since 1.0
 */
public class XmlElementBuilder {

	// Fields
	private final Element element;

	/**
	 * Create a new Element instance.
	 *
	 * @param name The name of the element (required, not empty)
	 * @param document The parent document (required)
	 */
	public XmlElementBuilder(final String name, final Document document) {
		Assert.hasText(name, "Element name required");
		Assert.notNull(document, "Owner document required");
		element = document.createElement(name);
	}

	/**
	 * Add an attribute to the current element.
	 *
	 * @param qName The attribute name (required, not empty)
	 * @param value The value of the attribute (required)
	 * @return the current XmlElementBuilder
	 */
	public XmlElementBuilder addAttribute(final String qName, final String value) {
		Assert.hasText(qName, "Attribute qName required");
		Assert.notNull(value, "Attribute value required");
		element.setAttribute(qName, value);
		return this;
	}

	/**
	 * Add a child node to the current element.
	 *
	 * @param node The new node (required)
	 * @return The builder for the current element
	 */
	public XmlElementBuilder addChild(final Node node) {
		Assert.notNull(node, "Node required");
		this.element.appendChild(node);
		return this;
	}

	/**
	 * Add text contents to the current element. This will overwrite
	 * any previous text content.
	 *
	 * @param text The text content (required, not empty)
	 * @return The builder for the current element
	 */
	public XmlElementBuilder setText(final String text) {
		Assert.hasText(text, "Text content required");
		element.setTextContent(text);
		return this;
	}

	/**
	 * Get the element instance.
	 *
	 * @return The element.
	 */
	public Element build() {
		return element;
	}
}
