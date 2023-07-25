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
package eu.dzhw.zofar.management.generator.qml.slc;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.utils.files.FileClient;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public abstract class GenericGenerator {
	public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<zofar:questionnaire xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "	xmlns:zofar=\"http://www####/zofar/xml/questionnaire\" xmlns:display=\"http://www.####/zofar/xml/display\"\n"
			+ "	language=\"de\">";
	public static final String FOOTER = "</zofar:questionnaire>";
	protected final FileClient fileC = FileClient.getInstance();
	protected final ReplaceClient replaceC = ReplaceClient.getInstance();
	protected final XmlClient xmlC = XmlClient.getInstance();
	public GenericGenerator() {
		super();
	}
	protected QuestionnaireDocument getDocument(final String xml) {
		try {
			return QuestionnaireDocument.Factory.parse(xml);
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected XmlObject docToXmlObject(final QuestionnaireDocument doc) throws TransformerException, XmlException {
		final Node node = doc.getDomNode();
		DOMSource domSource = new DOMSource(node);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		final XmlObject back = XmlObject.Factory.parse(writer.toString());
		DocumentType doctype = ((org.w3c.dom.Document)node).getDoctype();
		if (doctype != null) {		
			XmlDocumentProperties props = back.documentProperties();
			props.setDoctypeName(doctype.getName());
			props.setDoctypePublicId(doctype.getPublicId());
			props.setDoctypeSystemId(doctype.getSystemId());
		}
		return back;
	}
	public String generatePrefix(String template, List<Map<String, String>> replacements) {
		return replacePlaceholder(template,replacements);
	}
	public String generatePostfix(String template, List<Map<String, String>> replacements) {
		return replacePlaceholder(template,replacements);
	}
	public abstract String process(String template, List<Map<String, String>> replacements);
	public String replacePlaceholder(String template, List<Map<String, String>> replacements) {
		return this.replacePlaceholder(template, replacements, "[[", "]]");
	}
	public String replacePlaceholder(String template, List<Map<String, String>> replacements,final String opener, final String closer) {
		final StringBuffer back = new StringBuffer();
		template = template.replaceAll("<xml-fragment [^>]*>", "").replaceAll("</xml-fragment>", "");
		if((replacements == null))return template;
		if((replacements.isEmpty()))return template;
		String generatedStr = template;
		for(Map<String, String> replacementMap : replacements) {
			for (Map.Entry<String, String> replacement : replacementMap.entrySet()) {
				generatedStr = replaceC.replaceTag(generatedStr, opener + replacement.getKey() + closer,
						replacement.getValue());
			}
		}
		back.append(generatedStr);
		return back.toString();
	}
}
