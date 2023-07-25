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
public class OpenFieldIntegrity extends AbstractVerificationTest {
	public OpenFieldIntegrity() {
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
	public void testOpenFields() {
		MessageProvider.info(this,"test open fields ...");
	    final Set<String> uncomplete = new HashSet<String>();
//	    final NodeList usingNodes = this.getByXPath("//parent::questionOpen[@type]");
//	    final NodeList usingNodes = this.getByXPath("//parent::questionOpen[@type] | //parent::attachedOpen[@type]");
	    final NodeList usingNodes = this.getByXPath("//*[@type]");
	    final int count = usingNodes.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) usingNodes.item(i);
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final String path = this.createPath(e);
	    	final String nodeName = e.getNodeName();
	    	if(nodeName.equals("zofar:variable"))continue;
	    	if(nodeName.equals("zofar:responseDomain"))continue;
	    	final NamedNodeMap nodeAttributes = e.getAttributes();
	    	final Node typeNode = nodeAttributes.getNamedItem("type");
	    	final String type = typeNode.getNodeValue();
	    	final Node messageNode = nodeAttributes.getNamedItem("validationMessage");
	    	if((!type.equals("text"))&&(!type.equals("monthpicker"))&&(messageNode == null))uncomplete.add(path);
	    }
	    TestCase.assertTrue("uids without required validationMessage found "+uncomplete,uncomplete.isEmpty());
	}
}
