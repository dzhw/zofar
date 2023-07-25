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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.TransformerException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public class LoopGenerator extends GenericGenerator {
	private static LoopGenerator INSTANCE;
	private LoopGenerator() {
		super();
	}
	public static LoopGenerator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new LoopGenerator();
		return INSTANCE;
	}
	@Override
	public String process(String template, List<Map<String, String>> replacements) {
		XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		opts.setSaveOuter();
		opts.setSaveAggressiveNamespaces();
		HashMap<String, String> nsMap = new HashMap<String, String>();
		nsMap.put("http://www####/zofar/xml/questionnaire", "zofar");
		opts.setSaveSuggestedPrefixes(nsMap);
		final QuestionnaireDocument doc = getDocument(GenericGenerator.HEADER + template + GenericGenerator.FOOTER);
		try {
			String generatedStr = XmlClient.getInstance().convert2String(docToXmlObject(doc), opts).replaceAll("<zofar:questionnaire [^>]*>", "").replaceAll("</zofar:questionnaire>", "");
			return super.replacePlaceholder(generatedStr, replacements);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return null;
	}
}
