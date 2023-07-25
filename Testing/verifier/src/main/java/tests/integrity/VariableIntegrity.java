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
package tests.integrity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tests.common.AbstractVerificationTest;
import tests.common.MessageProvider;
public class VariableIntegrity extends AbstractVerificationTest {
	private Map<String,Node> declaredVariables;
	public VariableIntegrity() {
		super();
	}
	@Before
	public void setUp() throws Exception {
		super.setUp();
		final NodeList result = this.getByXPath("/questionnaire/variables/*");
		declaredVariables = new HashMap<String,Node>();
		final int count = result.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) result.item(i);
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap attributes = e.getAttributes();
	    	final Node name = attributes.getNamedItem("name");
	    	declaredVariables.put(name.getNodeValue(),e);
	    }
	}
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	@Test
	public void testUndeclaredVariables() {
		MessageProvider.info(this,"test undeclared variables ...");
	    final Set<String> notDeclaredVariables = new HashSet<String>();
	    final NodeList usingNodes = this.getByXPath("//*[@variable]");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(e == null)continue;
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node variableNode = nodeAttributes.getNamedItem("variable");
	    	final String name = variableNode.getTextContent();
	    	final String nodeType = e.getNodeName();
	    	if(nodeType.equals("zofar:preloadItem")){
	    		MessageProvider.info(this,"skip preload variable "+name);
	    		continue;
	    	}
	    	if(!declaredVariables.keySet().contains(name)){
	    		notDeclaredVariables.add(name);
	    	}
	    }
	    TestCase.assertTrue("Undeclared Variables found "+notDeclaredVariables,notDeclaredVariables.isEmpty());
	}
	@Test
	public void testMultipleDeclaredVariables() {
		MessageProvider.info(this,"test multiple declared variables ...");
		final Set<String> alreadyUsedVariables = new HashSet<String>();
		final Set<String> doubleDeclaredVariables = new HashSet<String>();
		final NodeList result = this.getByXPath("/questionnaire/variables/*");
		declaredVariables = new HashMap<String,Node>();
		final int count = result.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) result.item(i);
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap attributes = e.getAttributes();
	    	final Node name = attributes.getNamedItem("name");
	    	if(alreadyUsedVariables.contains(name.getNodeValue()))doubleDeclaredVariables.add(name.getNodeValue());
	    	else alreadyUsedVariables.add(name.getNodeValue());
	    }
	    TestCase.assertTrue("Multiple time declared Variables found "+doubleDeclaredVariables,doubleDeclaredVariables.isEmpty());
	}
	@Test
	public void testMultipleUsedVariables() {
		MessageProvider.info(this,"test multiple used variables ...");
	    final Set<String> alreadyUsedVariables = new HashSet<String>();
	    final Set<String> doubleUsedVariables = new HashSet<String>();
//	    final NodeList usingNodes = this.getByXPath("//@variable");
	    final NodeList usingNodes = this.getByXPath("//*[@variable]");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(e == null)continue;
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final String nodeType = e.getNodeName();
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node variableNode = nodeAttributes.getNamedItem("variable");
	    	boolean skip = false;
		    if(nodeType.equals("zofar:preloadItem"))skip = true;
	    	final String name = variableNode.getTextContent();
	    	if(alreadyUsedVariables.contains(name)&&(!skip)){
	    		doubleUsedVariables.add(name);
	    	}
	    	alreadyUsedVariables.add(name);
	    }
	    if(!doubleUsedVariables.isEmpty())MessageProvider.warn(this,"Multiple time used Variables found "+doubleUsedVariables);
	    TestCase.assertTrue("Multiple time used Variables found "+doubleUsedVariables,true);
	}
	@Test
	public void testMultipleUsedConstructedDropDownVariables() {
		MessageProvider.info(this,"test multiple used constructed variables in DropDown...");
	    final Set<String> alreadyUsedVariables = new HashSet<String>();
	    final Set<String> doubleUsedVariables = new HashSet<String>();
	    final NodeList usingNodes = this.getByXPath("//*[@type='dropdown']");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(e == null)continue;
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node variableNode = nodeAttributes.getNamedItem("variable");
	    	final String variable = variableNode.getTextContent();
	    	if(alreadyUsedVariables.contains(variable)){
	    		doubleUsedVariables.add(variable);
	    	}
	    	else alreadyUsedVariables.add(variable);
	    }
	    if(!doubleUsedVariables.isEmpty())MessageProvider.warn(this,"Multiple time used constructed Variables in DropDown found "+doubleUsedVariables);
	    TestCase.assertTrue("Multiple time used constructed Variables in DropDown found "+doubleUsedVariables,true);
	}
	@Test
	public void testVariableTypes() {
		MessageProvider.info(this,"test variable types ...");
	    final Map<String,String> wrongTypeVariables = new HashMap<String,String>();
//	    final NodeList usingNodes = this.getByXPath("//parent::*[@variable]");
	    final NodeList usingNodes = this.getByXPath("//*[@variable]");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node variableNode = nodeAttributes.getNamedItem("variable");
	    	final String nodeType = e.getNodeName();
	    	final String variable = variableNode.getTextContent();
	    	if(nodeType.equals("zofar:preloadItem")){
	    		MessageProvider.info(this,"skip preload variable "+variable);
	    		continue;
	    	}
	    	Node declaredVariable = (Node)declaredVariables.get(variable);
	    	if(declaredVariable == null)continue;
	    	final NamedNodeMap declaredAttributes = declaredVariable.getAttributes();
	    	final Node typeNode = declaredAttributes.getNamedItem("type");
	    	final String declaredVariableType = typeNode.getTextContent();
	    	boolean wrong = true;
	    	if(nodeType.equals("zofar:responseDomain") && declaredVariableType.equals("singleChoiceAnswerOption"))wrong = false;
	    	else if(nodeType.equals("zofar:questionOpen")) {
	    		if(declaredVariableType.equals("string"))wrong = false;
		    	final Node questionTypeNode = nodeAttributes.getNamedItem("type");
		    	if(wrong && (questionTypeNode != null)) {
		    		final String questionType = questionTypeNode.getNodeValue();
		    		if(questionType.contentEquals("number") && declaredVariableType.equals("number"))wrong = false;
		    	}
	    	}
	    	else if(nodeType.equals("zofar:attachedOpen")) {
	    		if(declaredVariableType.equals("string"))wrong = false;
		    	final Node questionTypeNode = nodeAttributes.getNamedItem("type");
		    	if(wrong && (questionTypeNode != null)) {
		    		final String questionType = questionTypeNode.getNodeValue();
		    		if(questionType.contentEquals("number") && declaredVariableType.equals("number"))wrong = false;
		    	}
	    	}
	    	else if(nodeType.equals("zofar:question") && declaredVariableType.equals("string"))wrong = false;
	    	else if(nodeType.equals("zofar:episodes") && declaredVariableType.equals("string"))wrong = false;
	    	else if(nodeType.equals("zofar:episodesTable") && declaredVariableType.equals("string"))wrong = false;
	    	else if(nodeType.equals("zofar:answerOption") && declaredVariableType.equals("boolean"))wrong = false;
	    	else if(nodeType.equals("zofar:SlotItem") && declaredVariableType.equals("boolean"))wrong = false;
	    	else if(nodeType.equals("zofar:left") && declaredVariableType.equals("singleChoiceAnswerOption"))wrong = false;
	    	else if(nodeType.equals("zofar:right") && declaredVariableType.equals("singleChoiceAnswerOption"))wrong = false;
	    	else if(nodeType.equals("zofar:variable"))wrong = false;
	    	else{
	    		Node parent = e.getParentNode();
	    		if(parent != null){
	    			final String parentType = parent.getNodeName();
	    			if(parentType.equals("zofar:triggers"))wrong = false;
	    		}
	    	}
	    	if(wrong){
		    	final String path = this.createPath(e);
		    	MessageProvider.error(this,"parent question found for {} ==> {}",path+" ("+variableNode+")",nodeType);
	    		wrongTypeVariables.put(path,variable);
	    	}
	    }
	    TestCase.assertTrue("Incompatiple Variable types found "+wrongTypeVariables,wrongTypeVariables.isEmpty());
	}
}
