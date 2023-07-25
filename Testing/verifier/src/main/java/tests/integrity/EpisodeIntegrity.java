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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
import junit.framework.TestCase;
import tests.common.AbstractVerificationTest;
import tests.common.MessageProvider;
public class EpisodeIntegrity extends AbstractVerificationTest {
	private Map<String,Node> episodePages;
	private Map<String,Node> nonEpisodePages;
	public EpisodeIntegrity() {
		super();
	}
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		episodePages = getEpisodePages();
		nonEpisodePages = getNonEpisodePages(episodePages);
	}
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	private Map<String,Node> getDeclaredVariables(){
		final NodeList result = this.getByXPath("/questionnaire/variables/*");
		final Map<String,Node> declaredVariables = new HashMap<String,Node>();
		final int count = result.getLength();
	    for (int i = 0; i < count; ++i) {
	    	Node e = (Node) result.item(i);
	    	if(this.hasParent(e, "zofar:researchdata"))continue;
	    	final NamedNodeMap attributes = e.getAttributes();
	    	final Node name = attributes.getNamedItem("name");
	    	declaredVariables.put(name.getNodeValue(),e);
	    }
	    return declaredVariables;
	}
	private Map<String,Node> getEpisodePages() {
		final NodeList result = this.getByXPath("page");
		final int count = result.getLength();
		final Map<String,Node> back = new LinkedHashMap<String,Node>();
		for (int i = 0; i < count; ++i) {
			final Node e = result.item(i);
			if(this.hasParent(e, "zofar:researchdata"))continue;
			final NamedNodeMap attributes = e.getAttributes();
			final Node pageName = attributes.getNamedItem("uid");
			final String pageNameStr = pageName.getNodeValue();
			final NodeList triggerList = this.getByXPath(e, "triggers/*");
			final int triggerCount = triggerList.getLength();
			for (int j = 0; j < triggerCount; ++j) {
				final Node trigger = triggerList.item(j);
				if(this.hasParent(trigger, "zofar:researchdata"))continue;
				final NamedNodeMap triggerAttributes = trigger.getAttributes();
				final String triggerType = trigger.getNodeName();
				final NodeList scriptItemList = this.getByXPath(trigger, "descendant::scriptItem");
				final int scriptItemCount = scriptItemList.getLength();
				for (int k = 0; k < scriptItemCount; ++k) {
					final Node scriptItemNode = scriptItemList.item(k);
					final NamedNodeMap scriptItemAttributes = scriptItemNode.getAttributes();
					final String commandStr = scriptItemAttributes.getNamedItem("value").getTextContent();
					if(commandStr.contains("zofar.frac(")) back.put(pageNameStr, e);
					if(commandStr.contains("zofar.defrac(zofar.list(")) back.put(pageNameStr, e);
				}
				if(triggerType.contentEquals("zofar:action")) {
					final Node commandNode = triggerAttributes.getNamedItem("command");
					if(commandNode == null)continue;
					final String commandStr = commandNode.getTextContent();
					if(commandStr.contains("zofar.frac(")) back.put(pageNameStr, e);
					if(commandStr.contains("zofar.defrac(zofar.list(")) back.put(pageNameStr, e);
				}
			}
		}
		return back;
	}
	private Map<String,Node> getNonEpisodePages(final Map<String,Node> episodePages) {
		final NodeList result = this.getByXPath("page");
		final int count = result.getLength();
		final Set<String> episodePageNames = episodePages.keySet();
		final Map<String,Node> back = new LinkedHashMap<String,Node>();
		for (int i = 0; i < count; ++i) {
			final Node e = result.item(i);
			if(this.hasParent(e, "zofar:researchdata"))continue;
			final NamedNodeMap attributes = e.getAttributes();
			final Node pageName = attributes.getNamedItem("uid");
			final String pageNameStr = pageName.getNodeValue();
			if(episodePageNames.contains(pageNameStr))continue;
			back.put(pageNameStr,e);
		}
		return back;
	}
	private List<String> getVariablesFromeNode(final Node rootNode){
	    final NodeList usingNodes = this.getByXPath(rootNode,"descendant::*[@variable]");
	    final List<String> back = new ArrayList<String>();
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
	    	back.add(variable);
	    }
		return back;
	}
	private List<String> getFragmentVariablesFromeNode(final Node rootNode){
	    final List<String> commands = new ArrayList<String>();
		final NodeList triggerList = this.getByXPath(rootNode, "triggers/*");
		final int triggerCount = triggerList.getLength();
		for (int j = 0; j < triggerCount; ++j) {
			final Node trigger = triggerList.item(j);
			if(this.hasParent(trigger, "zofar:researchdata"))continue;
			final NodeList scriptItemList = this.getByXPath(trigger, "descendant::scriptItem");
			final int scriptItemCount = scriptItemList.getLength();
			for (int k = 0; k < scriptItemCount; ++k) {
				final Node scriptItemNode = scriptItemList.item(k);
				final NamedNodeMap scriptItemAttributes = scriptItemNode.getAttributes();
				final String commandStr = scriptItemAttributes.getNamedItem("value").getTextContent();
				if(commandStr.startsWith("zofar.frac(")) commands.add(commandStr);
				if(commandStr.contains("zofar.defrac(zofar.list(")) commands.add(commandStr);
			}
			final NamedNodeMap triggerAttributes = trigger.getAttributes();
			final String triggerType = trigger.getNodeName();
			if(triggerType.contentEquals("zofar:action")) {
				final Node commandNode = triggerAttributes.getNamedItem("command");
				if(commandNode == null)continue;
				final String commandStr = commandNode.getTextContent();
				if(commandStr.contains("zofar.frac(")) commands.add(commandStr);
				if(commandStr.contains("zofar.defrac(zofar.list(")) commands.add(commandStr);
			}
		}
		final List<String> back = new ArrayList<String>();
		for(final String command:commands) {
			String pattern = "";
			int group = 0;
			boolean skip = false;
			if(command.startsWith("zofar.log("))skip = true;
			if(!skip) {
				if(command.contains("zofar.frac(zofar.list(")) {
					pattern = "(" + Pattern.quote("zofar.frac(zofar.list(") + ")(((?!" + Pattern.quote(")") + ").)*)(" + Pattern.quote(")") + ")";
					group = 2;
				}
				if(command.contains("zofar.defrac(zofar.list(")) {
					pattern = "(" + Pattern.quote("zofar.defrac(zofar.list(") + ")(((?!" + Pattern.quote(")") + ").)*)(" + Pattern.quote(")") + ")";
					group = 2;
				}
				final List<String> found = ReplaceClient.getInstance().findInString(pattern, command, false,group);
				if(found == null)continue;
				if(found.isEmpty())continue;
				for(final String tmp : found) {
					back.addAll(Arrays.asList(tmp.split(",")));
				}
			}
		}
		return back;
	}
	@Test
	public void testEpisodeVariablesUsage() {
		MessageProvider.info(this, "test usage of episode variables outside of modules ...");
		final List<String> episodeVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : episodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
			episodeVariables.addAll(usedVariables);
		}
		final List<String> nonEpisodeVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : nonEpisodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
			nonEpisodeVariables.addAll(usedVariables);
		}
		episodeVariables.removeAll(nonEpisodeVariables);
		final Map<String,List<String>> usedOutside = new HashMap<String,List<String>>();
		for(final Map.Entry<String, Node> page : nonEpisodePages.entrySet()) {
			final String pageName = page.getKey().strip();
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
		    for(final String usedVariable:usedVariables) {
		    	if(episodeVariables.contains(usedVariable)){
		    		List<String> pageList = null;
		    		if(usedOutside.containsKey(usedVariable)) pageList =usedOutside.get(usedVariable);
		    		if(pageList == null)pageList = new ArrayList<String>();
		    		if(!pageList.contains(pageName))pageList.add(pageName);
		    		usedOutside.put(usedVariable,pageList);
		    	}
		    }
		}
		TestCase.assertTrue("Usage of Episode Variables outside of modules found "+usedOutside,usedOutside.isEmpty());
	}
	@Test
	public void testFragmentVariablesDeclaration() {
		MessageProvider.info(this, "test declaration of fragment variables ...");
		final List<String> usedVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : episodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> usedOnPage = this.getFragmentVariablesFromeNode(pageNode);
			if(usedOnPage == null)continue;
			if(usedOnPage.isEmpty())continue;
			usedVariables.addAll(usedOnPage);
		}
		final Map<String,Node> declaredVariables = getDeclaredVariables();
	    final Set<String> notDeclaredVariables = new HashSet<String>();
	    for(final String usedVariable:usedVariables) {
	    	if(!declaredVariables.keySet().contains(usedVariable)){
	    		notDeclaredVariables.add(usedVariable);
	    		continue;
	    	}
	    	Node declaredVariable = (Node)declaredVariables.get(usedVariable);
	    	if(declaredVariable == null)continue;
	    	final NamedNodeMap declaredAttributes = declaredVariable.getAttributes();
	    	final Node typeNode = declaredAttributes.getNamedItem("type");
	    	final String declaredVariableType = typeNode.getTextContent();
	    	if(!declaredVariableType.contentEquals("string")){
	    		notDeclaredVariables.add(usedVariable);
	    		continue;
	    	}
	    }
	    TestCase.assertTrue("Missing Declaration of Fragment Variables or Declaration Type is not String ("+notDeclaredVariables.size()+") "+notDeclaredVariables,notDeclaredVariables.isEmpty());
	}
	@Test
	public void testFragmentVariablesUsage() {
		MessageProvider.info(this, "test usage of fragment variables outside of modules ...");
		final List<String> fragmentVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : episodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> fragmentVariablesOnPage = this.getFragmentVariablesFromeNode(pageNode);
			if(fragmentVariablesOnPage == null)continue;
			if(fragmentVariablesOnPage.isEmpty())continue;
			fragmentVariables.addAll(fragmentVariablesOnPage);
		}
		final Map<String,List<String>> usedOutside = new HashMap<String,List<String>>();
		for(final Map.Entry<String, Node> page : nonEpisodePages.entrySet()) {
			final String pageName = page.getKey();
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
		    for(final String usedVariable:usedVariables) {
		    	if(fragmentVariables.contains(usedVariable)){
		    		List<String> pageList = null;
		    		if(usedOutside.containsKey(usedVariable)) pageList =usedOutside.get(usedVariable);
		    		if(pageList == null)pageList = new ArrayList<String>();
		    		pageList.add(pageName);
		    		usedOutside.put(usedVariable,pageList);
		    	}
		    }
		}
	    TestCase.assertTrue("Usage of Fragment Variables outside of modules found "+usedOutside,usedOutside.isEmpty());
	}
	@Test
	public void testNonEpisodeVariablesUsage() {
		MessageProvider.info(this, "test usage of non episode variables inside of modules ...");
		final List<String> nonEpisodeVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : nonEpisodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
			nonEpisodeVariables.addAll(usedVariables);
		}
		final List<String> episodeVariables = new ArrayList<String>();
		for(final Map.Entry<String, Node> page : episodePages.entrySet()) {
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
			episodeVariables.addAll(usedVariables);
		}
		nonEpisodeVariables.removeAll(episodeVariables);
		final Map<String,List<String>> usedOutside = new HashMap<String,List<String>>();
		for(final Map.Entry<String, Node> page : episodePages.entrySet()) {
			final String pageName = page.getKey().strip();
			final Node pageNode = page.getValue();
			final List<String> usedVariables = getVariablesFromeNode(pageNode);
		    for(final String usedVariable:usedVariables) {
		    	if(nonEpisodeVariables.contains(usedVariable)){
		    		List<String> pageList = null;
		    		if(usedOutside.containsKey(usedVariable)) pageList =usedOutside.get(usedVariable);
		    		if(pageList == null)pageList = new ArrayList<String>();
		    		if(!pageList.contains(pageName))pageList.add(pageName);
		    		usedOutside.put(usedVariable,pageList);
		    	}
		    }
		}
		TestCase.assertTrue("Usage of non episode Variables inside of modules found ("+usedOutside.size()+") "+usedOutside,usedOutside.isEmpty());
	}
}
