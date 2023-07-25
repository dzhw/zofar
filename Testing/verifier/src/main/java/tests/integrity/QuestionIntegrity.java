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
import java.util.HashSet;
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
public class QuestionIntegrity extends AbstractVerificationTest {
	public QuestionIntegrity() {
		super();
	}
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	@Test
	public void testNoHeader() {
		MessageProvider.info(this,"test questions for no header ...");
	    final Set<String> noHeader = new HashSet<String>();
	    final NodeList usingNodes = this.getByXPath("//parent::responseDomain");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(e == null)continue;
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	Node parent = e.getParentNode();
	    	if(parent != null){
		    	final String nodeType = parent.getNodeName();
		    	if(nodeType.equals("zofar:item"))continue;
		    	NodeList headerList = this.getByXPath(parent,"header");
		    	if((headerList == null)||(headerList.getLength() == 0)){
		    		final String path = this.createPath(parent);
		    		noHeader.add(path);
		    	}
	    	}
	    }
	    TestCase.assertTrue("Questions without required header found "+noHeader,noHeader.isEmpty());
	}
//	    final NodeList mcRDCs = this.getByXPath("//parent::responseDomain//*[@exclusive]");
//	    		final NodeList mcItems = this.getByXPath(e,"//*[@exclusive]");
	@Test
	public void testNoAnswerOptions() {
		MessageProvider.info(this,"test questions for no Options ...");
	    final Set<String> noOptions = new HashSet<String>();
	    final NodeList usingNodes = this.getByXPath("//parent::responseDomain");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(e == null)continue;
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final String path = createPath(e);
	    	final String type = e.getNodeName();
	    	boolean empty = true;
	    	if(type.equals("zofar:responseDomain")){
		    	NodeList optionsList = this.getByXPath(e,"descendant::answerOption | descendant::question[@variable] | descendant::questionOpen[@variable]");
		    	if((optionsList == null)||(optionsList.getLength() == 0)){
		    	}
		    	else{
		    		empty = false;
		    	}
	    	}
	    	if(empty){
	    		noOptions.add(path);
	    	}
	    }
	    TestCase.assertTrue("Questions without AnswerOptions/Open Questions found "+noOptions,noOptions.isEmpty());
	}
}
