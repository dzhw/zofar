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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.TransformerException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.QuestionnaireType;
import de.his.zofar.xml.questionnaire.VariableType;
import de.his.zofar.xml.questionnaire.VariablesType;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public class ModuleGenerator extends GenericGenerator {
	private static ModuleGenerator INSTANCE;
	private ModuleGenerator() {
		super();
	}
	public static ModuleGenerator getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ModuleGenerator();
		return INSTANCE;
	}
	@Override
	public String generatePrefix(String template, List<Map<String, String>> replacements) {
		return super.replacePlaceholder(template, replacements);
	}
	@Override
	public String generatePostfix(String template, List<Map<String, String>> replacements) {
		return super.replacePlaceholder(template, replacements);
	}
	@Override
	public String process(String template, List<Map<String, String>> replacements) {
		final XmlOptions opts = new XmlOptions();
		opts.setCharacterEncoding("utf8");
		opts.setSavePrettyPrint();
		opts.setSavePrettyPrintIndent(4);
		opts.setSaveOuter();
		opts.setSaveAggressiveNamespaces();
		final HashMap<String, String> nsMap = new HashMap<String, String>();
		nsMap.put("http://www####/zofar/xml/questionnaire", "zofar");
		opts.setSaveSuggestedPrefixes(nsMap);
		final QuestionnaireDocument doc = getDocument(GenericGenerator.HEADER + template + GenericGenerator.FOOTER);
		final QuestionnaireType questionnaire = doc.getQuestionnaire();
		VariablesType variableContainer = questionnaire.getVariables();
		if (variableContainer == null)
			variableContainer = questionnaire.addNewVariables();
		final Map<String, VariableType> declaredVariables = new LinkedHashMap<String, VariableType>();
		if (variableContainer != null) {
			final VariableType[] variableObjs = variableContainer.getVariableArray();
			if (variableObjs != null) {
				final int count = variableObjs.length;
				for (int a = 0; a < count; a++) {
					final VariableType variable = variableObjs[a];
					declaredVariables.put(variable.getName(), variable);
				}
			}
		}
		final List<VariableType> notDeclaredVariables = new ArrayList<VariableType>();
		final XmlObject[] usedVariables = XmlClient.getInstance().getByXPath(doc, "//*[@variable]");
		if (usedVariables != null) {
			final int count = usedVariables.length;
			for (int a = 0; a < count; a++) {
				final XmlObject useVariable = usedVariables[a];
				final Node node = useVariable.getDomNode();
				final NamedNodeMap nodeAttributes = node.getAttributes();
				final Node variableNode = nodeAttributes.getNamedItem("variable");
				final String name = variableNode.getNodeValue();
				final String nodeType = node.getNodeName();
				de.his.zofar.xml.questionnaire.VariableType.Type.Enum varType = null;
				if (nodeType.equals("zofar:responseDomain"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION;
				if (nodeType.equals("zofar:questionOpen"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.STRING;
				if (nodeType.equals("zofar:question"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.STRING;
				if (nodeType.equals("zofar:answerOption"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.BOOLEAN;
				if (nodeType.equals("zofar:left"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION;
				if (nodeType.equals("zofar:right"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION;
				if (nodeType.equals("zofar:SlotItem"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.BOOLEAN;
				if (nodeType.equals("zofar:variable"))
					varType = de.his.zofar.xml.questionnaire.VariableType.Type.STRING;
				if (varType != null) {
					if (!declaredVariables.containsKey(name)) {
						VariableType newVar = VariableType.Factory.newInstance();
						newVar.setName(name);
						newVar.setType(varType);
						notDeclaredVariables.add(newVar);
					}
				}
			}
		}
		final List<VariableType> vars = new ArrayList<VariableType>();
		if (!declaredVariables.isEmpty())
			vars.addAll(declaredVariables.values());
		if (!notDeclaredVariables.isEmpty()) {
			vars.addAll(getCleaned(notDeclaredVariables));
		}
		variableContainer.setVariableArray(vars.toArray(new VariableType[0]));
		try {
			String generatedStr = XmlClient.getInstance().convert2String(docToXmlObject(doc), opts);
			return super.replacePlaceholder(generatedStr, replacements);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		return null;
	}
	public List<VariableType> getCleaned(List<VariableType> list) {
		List<VariableType> myList = new ArrayList<VariableType>();
		for (VariableType var : list) {
			String name=var.getName();
			if (!getDuplicate(myList,name)) {
				myList.add(var);
			}
		}
		return myList;
	}
	public boolean getDuplicate(List<VariableType> list, String name) {
		boolean duplicate=false;
		for (VariableType var : list) {
			if (var.getName().equals(name)) {
				duplicate=true;
				break;
			}
		}
		return duplicate;
	}
}
