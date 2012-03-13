package org.springframework.shell.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class PluginConfigurationReader {

	
	private static final String CONFIGURATION = "configuration";
	
	private static final String CONFIGURATION_CLASS_NAME = "class";
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private final ResourcePatternResolver resourcePatternResolver;
	
	public PluginConfigurationReader(ResourcePatternResolver resourcePatternResolver) {
		Assert.notNull(resourcePatternResolver, "ResourceLoader must not be null");
		this.resourcePatternResolver = resourcePatternResolver;
	}
	
	
	public PluginInfo[] readPluginInfos(String... pluginInfoXmlLocations) {
		ErrorHandler handler = new SimpleSaxErrorHandler(logger);
		List<PluginInfo> infos = new LinkedList<PluginInfo>();
		String resourceLocation = null;
		try {
			for (String location : pluginInfoXmlLocations) {
				Resource[] resources = this.resourcePatternResolver.getResources(location);
				for (Resource resource : resources) {
					resourceLocation = resource.toString();
					InputStream stream = resource.getInputStream();
					try {
						Document document = buildDocument(handler, stream);
						parseDocument(resource, document, infos);
					}
					finally {
						stream.close();
					}
				}
			}
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Cannot parse persistence unit from " + resourceLocation, ex);
		}
		catch (SAXException ex) {
			throw new IllegalArgumentException("Invalid XML in persistence unit from " + resourceLocation, ex);
		}
		catch (ParserConfigurationException ex) {
			throw new IllegalArgumentException("Internal error parsing persistence unit from " + resourceLocation);
		}

		return infos.toArray(new PluginInfo[infos.size()]);
	}
	

	/**
	 * Parse the validated document and add entries to the given unit info list.
	 */
	protected List<PluginInfo> parseDocument(
			Resource resource, Document document, List<PluginInfo> infos) throws IOException {

		Element persistence = document.getDocumentElement();
		List<Element> configurations = DomUtils.getChildElementsByTagName(persistence, CONFIGURATION);
		for (Element configuration : configurations) {
			PluginInfo info = parsePluginInfo(configuration);				
			infos.add(info);
			
		}

		return infos;
	}

	/**
	 * Parse the plugin DOM element.
	 */
	protected PluginInfo parsePluginInfo(Element configuration) throws IOException { 
		PluginInfo pluginInfo = new PluginInfo();		
		parseClass(configuration, pluginInfo);
		return pluginInfo;
	}
	
	
	/**
	 * Parse the <code>class</code> XML elements.
	 */
	@SuppressWarnings("unchecked")
	protected void parseClass(Element configuration, PluginInfo pluginInfo) {
		List<Element> classes = DomUtils.getChildElementsByTagName(configuration, CONFIGURATION_CLASS_NAME);
		for (Element element : classes) {
			String value = DomUtils.getTextValue(element).trim();
			if (StringUtils.hasText(value))
				pluginInfo.addConfigurationClassName(value);
		}
	}

	/**
	 * Validate the given stream and return a valid DOM document for parsing.
	 */
	protected Document buildDocument(ErrorHandler handler, InputStream stream)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder parser = dbf.newDocumentBuilder();
		parser.setErrorHandler(handler);
		return parser.parse(stream);
	}
}
