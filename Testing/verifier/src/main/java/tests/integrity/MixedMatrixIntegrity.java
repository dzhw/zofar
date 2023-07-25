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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tests.common.AbstractVerificationTest;
import tests.common.MessageProvider;

public class MixedMatrixIntegrity extends AbstractVerificationTest {

	public MixedMatrixIntegrity() {
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
	public void testItemBodies() {
		MessageProvider.info(this, "test mixed matrix question items for no body ...");

		final Set<String> noBody = new HashSet<String>();

		final NodeList usingNodes = this.getByXPath("//parent::matrixQuestionMixed");
		final int count = usingNodes.getLength();
		for (int i = 0; i < count; ++i) {
			Node e = (Node) usingNodes.item(i);
			if (e == null)continue;
			if(this.hasParent(e, "zofar:researchdata"))continue;
			final NodeList mixedChildNodes = e.getChildNodes();
			final int count1 = mixedChildNodes.getLength();
			for (int j = 0; j < count1; ++j) {
				Node mixedChild = (Node) mixedChildNodes.item(j);
				final String nodeType = mixedChild.getNodeName();
				if (nodeType.equals("zofar:responseDomain")) {
					final NodeList mixedItemNodes = mixedChild.getChildNodes();
					final int count2 = mixedItemNodes.getLength();
					for (int k = 0; k < count2; ++k) {
						Node mixedItem = (Node) mixedItemNodes.item(k);
						final String mixedItemType = mixedItem.getNodeName();
						if (mixedItemType.equals("zofar:item")) {
							boolean flag = false;
							
							final NodeList mixedItemChildNodes = mixedItem.getChildNodes();
							final int count3 = mixedItemChildNodes.getLength();
							for (int l = 0; l < count3; ++l) {
								Node mixedItemChild = (Node) mixedItemChildNodes.item(l);
								final String mixedItemChildType = mixedItemChild.getNodeName();
								if (mixedItemChildType.equals("zofar:body")) {
									flag = true;
									break;
								}
							}
							if(!flag){
								final String path = this.createPath(mixedItem);
								noBody.add(path);
							}
						}
					}
				}
			}
		}
		TestCase.assertTrue("Mixed Matrix Item without required body found " + noBody, noBody.isEmpty());

	}

}
