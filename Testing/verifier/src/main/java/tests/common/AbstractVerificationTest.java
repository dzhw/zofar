/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
package tests.common;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.ConfigurationException;
import org.custommonkey.xmlunit.jaxp13.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
@Ignore
public class AbstractVerificationTest extends TestCase {
	private Properties properties = null;
	private InputSource inputSource = null;
	private StreamSource streamSource = null;
	public AbstractVerificationTest() {
		this(System.getProperty("contentPath"), System
				.getProperty("schemaPath"), System.getProperty("runMode"));
	}
	private AbstractVerificationTest(final String contentPath,
			final String schemaPath, final String runMode) {
		super();
		this.setProperties(contentPath, schemaPath, runMode);
		XMLUnit.getTestDocumentBuilderFactory().setValidating(true);
	}
	private void setProperties(final String contentPath,
			final String schemaPath, final String runMode) {
		properties = new Properties();
		if ((runMode != null) && (!runMode.equals("file"))) {
			properties.put("runMode", runMode);
			properties.put("contentPath", contentPath);
			properties.put("schemaPath", schemaPath);
		} else {
			try {
				properties.load(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("system.properties"));
				properties.put("runMode", "configuration");
			} catch (FileNotFoundException e) {
				properties = null;
				MessageProvider.error(this,
						"Configuration file not found ==> {}", e);
			} catch (IOException e) {
				properties = null;
				MessageProvider.error(this,
						"Configuration file cannot be read ==> {}", e);
			}
		}
	}
	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}
	protected Validator getValidator() {
		try {
			final Validator validator = new Validator();
			if (getProperties().get("schemaPath") != null)
				validator.addSchemaSource(new StreamSource(new File(
						(String) (getProperties().get("schemaPath")))));
			return validator;
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected StreamSource getStreamSource() {
		if ((streamSource == null)
				&& (getProperties().get("contentPath") != null)) {
			streamSource = new StreamSource(new File((String) getProperties()
					.get("contentPath")));
		}
		return streamSource;
	}
	protected InputSource getInputSource() {
		if ((inputSource == null)
				&& (getProperties().get("contentPath") != null)) {
			inputSource = new InputSource((String) getProperties().get(
					"contentPath"));
		}
		return inputSource;
	}
	protected Document getDocument() {
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(this.getInputSource());
		} catch (Exception e) {
			MessageProvider.error(this, "cannot load Document ({})",
					e.getMessage());
		}
		return null;
	}
	protected NodeList getByXPath(final Object root, final String query) {
		try {
			final XPath xPath = XPathFactory.newInstance().newXPath();
			return (NodeList) xPath.evaluate(query, root,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			MessageProvider.error(this, "XPath error : {}", e.getMessage());
		}
		return null;
	}
	protected NodeList getByXPath(final String query) {
		return getByXPath(this.getDocument().getDocumentElement(), query);
	}
	protected String createPath(Node node) {
		String path = "";
		String nodename = node.getNodeName();
		while (nodename.startsWith("zofar") || nodename.startsWith("display")) {
			String uid = "";
			final NamedNodeMap parentAttributes = node.getAttributes();
			final Node parentUIDNode = parentAttributes.getNamedItem("uid");
			if (parentUIDNode != null)
				uid = parentUIDNode.getNodeValue();
			String separator = ":";
			if ((path.equals("")) || (uid.equals("")))
				separator = "";
			path = uid + separator + path;
			node = node.getParentNode();
			nodename = node.getNodeName();
		}
		return path;
	}
	protected boolean hasParent(final Node node, final String parentName) {
		if(node == null)return false;
		if(node.getNodeName().equals(parentName))return true;
		return hasParent(node.getParentNode(),parentName);
	}
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	protected boolean canRun() {
		return ((System.getProperty("suite") != null) && (System
				.getProperty("suite").equals("true")));
	}
	@Override
	public void run(TestResult result) {
		if (canRun())
			super.run(result);
	}
}
