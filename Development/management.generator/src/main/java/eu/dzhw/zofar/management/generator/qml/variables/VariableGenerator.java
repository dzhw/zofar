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
package eu.dzhw.zofar.management.generator.qml.variables;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import eu.dzhw.zofar.management.dev.qml.QMLClient;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
public class VariableGenerator {
	private static final VariableGenerator INSTANCE = new VariableGenerator();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VariableGenerator.class);
	private VariableGenerator() {
		super();
	}
	public static VariableGenerator getInstance() {
		return INSTANCE;
	}
	public String generate(final File qml) throws Exception{
		final StringBuffer back = new StringBuffer();
		final Document doc = XmlClient.getInstance().getDocument(qml.getAbsolutePath());
		final Map<String,Node> declaredVariables = new HashMap<String,Node>();
		final NodeList result = XmlClient.getInstance().getByXPath(doc,"/questionnaire/variables/*");
	    for (int i = 0; i < result.getLength(); ++i) {
	    	Node e = (Node) result.item(i);
	    	final NamedNodeMap attributes = e.getAttributes();
	    	final Node name = attributes.getNamedItem("name");
	    	declaredVariables.put(name.getNodeValue(),e);
	    }
	    final Map<String,Node> notDeclaredVariables = new LinkedHashMap<String,Node>();
	    final NodeList usingNodes = XmlClient.getInstance().getByXPath(doc,"//*[@variable]");
	    for (int i = 0; i < usingNodes.getLength(); ++i) {
	    	Node tmp = (Node) usingNodes.item(i);
	    	Node e = tmp;
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node variableNode = nodeAttributes.getNamedItem("variable");
	    	final String name = variableNode.getTextContent();
	    	if(!declaredVariables.keySet().contains(name)){
	    		notDeclaredVariables.put(name,e);
	    	}
	    }
	    if(!notDeclaredVariables.isEmpty()){
	    	Iterator<String> it = notDeclaredVariables.keySet().iterator();
	    	while(it.hasNext()){
	    		final String varname = it.next();
	    		final Node e = notDeclaredVariables.get(varname);
		    	final String nodeType = e.getNodeName();
		    	String varType = null;
		    	if(nodeType.equals("zofar:responseDomain"))varType = "singleChoiceAnswerOption";
		    	if(nodeType.equals("zofar:questionOpen") ) varType = "string";
		    	if(nodeType.equals("zofar:question"))varType = "string";
		    	if(nodeType.equals("zofar:answerOption"))varType="boolean";
		    	if(nodeType.equals("zofar:left"))varType="singleChoiceAnswerOption";
		    	if(nodeType.equals("zofar:right"))varType="singleChoiceAnswerOption";
		    	if(nodeType.equals("zofar:SlotItem"))varType="boolean";
		    	if(varType != null){
		    		back.append("<zofar:variable name=\""+varname+"\" type=\""+varType+"\" />\n");
		    	}
	    	}
	    }
	    return back.toString();
	}
}
