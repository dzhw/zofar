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
package eu.dzhw.zofar.testing.condition.qml;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
import eu.dzhw.zofar.testing.condition.term.elements.Element;
import eu.dzhw.zofar.testing.condition.term.elements.ElementVector;
public class QmlClient implements Serializable {
	private static final long serialVersionUID = -5278354520320971432L;
	private final static QmlClient INSTANCE = new QmlClient();
	private QmlClient() {
		super();
	}
	public static QmlClient getInstance() {
		return INSTANCE;
	}
	private boolean hasParent(final Node node, final String parentName) {
		if (node == null)
			return false;
		if (node.getNodeName().equals(parentName))
			return true;
		return hasParent(node.getParentNode(), parentName);
	}
	private Map<String,Set<Map<String,String>>> retrievePageTrigger(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		final Map<String,Set<Map<String,String>>> back = new LinkedHashMap<String,Set<Map<String,String>>>();
		final NodeList result = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "page");
		for (int i = 0; i < result.getLength(); ++i) {
			final Node e = result.item(i);
			if (this.hasParent(e, "zofar:researchdata"))
				continue;
			final NamedNodeMap attributes = e.getAttributes();
			final Node sourceNode = attributes.getNamedItem("uid");
			final String source = sourceNode.getNodeValue();
			Set<Map<String,String>> triggers = new LinkedHashSet<Map<String,String>>();
			final NodeList triggerList = XmlClient.getInstance().getByXPathUncached(e, "triggers/*");
			for (int j = 0; j < triggerList.getLength(); ++j) {
				final Node trigger = triggerList.item(j);
				if (this.hasParent(trigger, "zofar:researchdata"))
					continue;
				final NamedNodeMap triggerAttributes = trigger.getAttributes();
				final String triggerType = trigger.getNodeName();
				if(triggerType.contains("zofar:variable")) {
					final Node conditionNode = triggerAttributes.getNamedItem("condition");
					if (conditionNode != null) {
						final Map<String,String> conditions = new HashMap<String,String>();
						conditions.put("condition", conditionNode.getNodeValue().replaceAll(" {2,}", " "));
						triggers.add(conditions);
					}
				}
				else if (triggerType.contains("zofar:action")) {
					final Map<String,String> conditions = new HashMap<String,String>();
					final Node conditionNode = triggerAttributes.getNamedItem("condition");
					if (conditionNode != null) {
						conditions.put("condition", conditionNode.getNodeValue().replaceAll(" {2,}", " "));
					}
					final Node commandNode = triggerAttributes.getNamedItem("command");
					if (commandNode != null) {
						conditions.put("command", commandNode.getNodeValue().replaceAll(" {2,}", " "));
					}
					triggers.add(conditions);
				}
				else {
				}
				final NodeList scriptItemsList = XmlClient.getInstance().getByXPathUncached(trigger, "*");
				for (int k = 0; k < scriptItemsList.getLength(); ++k) {
					final Node scriptItem = scriptItemsList.item(k);
					final NamedNodeMap scriptItemAttributes = scriptItem.getAttributes();
					final Node valueNode = scriptItemAttributes.getNamedItem("value");
					if (valueNode != null) {
						final Map<String,String> conditions = new HashMap<String,String>();
						conditions.put("command", valueNode.getNodeValue().replaceAll(" {2,}", " "));
						triggers.add(conditions);
					}
				}
			}
			if(source != null){
				back.put(source, triggers);
			}
		}
		return back;
	}
	private Map<String,Set<Map<String,String>>> retrievePageTreeInitOrdered(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		final Map<String,Set<Map<String,String>>> back = new LinkedHashMap<String,Set<Map<String,String>>>();
		final NodeList result = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "page");
		for (int i = 0; i < result.getLength(); ++i) {
			final Node e = result.item(i);
			if (this.hasParent(e, "zofar:researchdata"))
				continue;
			final NamedNodeMap attributes = e.getAttributes();
			final Node sourceNode = attributes.getNamedItem("uid");
			final String source = sourceNode.getNodeValue();
			Set<Map<String,String>> transitions = new LinkedHashSet<Map<String,String>>();
			final NodeList transitionList = XmlClient.getInstance().getByXPathUncached(e, "transitions/*");
			boolean noUnconditioned = true;
			for (int j = 0; j < transitionList.getLength(); ++j) {
				final Node transition = transitionList.item(j);
				if (this.hasParent(transition, "zofar:researchdata"))
					continue;
				final NamedNodeMap transitionAttributes = transition.getAttributes();
				final Node targetNode = transitionAttributes.getNamedItem("target");
				final Node conditionNode = transitionAttributes.getNamedItem("condition");
				String target = null;
				if (targetNode != null)
					target = targetNode.getNodeValue();
				String condition = true + "";
				if (conditionNode != null)
					condition = conditionNode.getNodeValue();
				if(target != null){
					final Map<String,String> pair = new HashMap<String,String>();
					pair.put("target", target);
					pair.put("condition", condition.replaceAll(" {2,}", " "));
					transitions.add(pair);
					if(condition.equals("true"))noUnconditioned = false;
				}
			}
			if(noUnconditioned){
				final Map<String,String> pair = new HashMap<String,String>();
				pair.put("target", source);
				pair.put("condition", "true");
				transitions.add(pair);
			}
			if(source != null){
				back.put(source, transitions);
			}
		}
		return back;
	}
	private Map<String, Object> retrievePageTreeInit(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		final Map<String, Map<String, Set<String>>> forwardBack = new LinkedHashMap<String, Map<String, Set<String>>>();
		final Map<String, Set<String>> reverseBack = new LinkedHashMap<String, Set<String>>();
		final NodeList result = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "page");
		for (int i = 0; i < result.getLength(); ++i) {
			final Node e = result.item(i);
			if (this.hasParent(e, "zofar:researchdata"))
				continue;
			final NamedNodeMap attributes = e.getAttributes();
			final Node name = attributes.getNamedItem("uid");
			final NodeList transitionList = XmlClient.getInstance().getByXPathUncached(e, "transitions/*");
			for (int j = 0; j < transitionList.getLength(); ++j) {
				final Node transition = transitionList.item(j);
				if (this.hasParent(transition, "zofar:researchdata"))
					continue;
				final NamedNodeMap transitionAttributes = transition.getAttributes();
				final Node target = transitionAttributes.getNamedItem("target");
				final Node conditionNode = transitionAttributes.getNamedItem("condition");
				Map<String, Set<String>> edgeSet = null;
				if (forwardBack.containsKey(name.getNodeValue()))
					edgeSet = forwardBack.get(name.getNodeValue());
				if (edgeSet == null)
					edgeSet = new LinkedHashMap<String, Set<String>>();
				String condition = true + "";
				if (conditionNode != null)
					condition = conditionNode.getNodeValue();
				Set<String> conditions = null;
				if (edgeSet.containsKey(target.getNodeValue()))
					conditions = edgeSet.get(target.getNodeValue());
				if (conditions == null)
					conditions = new LinkedHashSet<String>();
				conditions.add(condition);
				edgeSet.put(target.getNodeValue(), conditions);
				forwardBack.put(name.getNodeValue(), edgeSet);
				Set<String> reverseEdgeSet = null;
				if (reverseBack.containsKey(target.getNodeValue()))
					reverseEdgeSet = reverseBack.get(target.getNodeValue());
				if (reverseEdgeSet == null)
					reverseEdgeSet = new LinkedHashSet<String>();
				reverseEdgeSet.add(name.getNodeValue());
				reverseBack.put(target.getNodeValue(), reverseEdgeSet);
			}
		}
		Map<String, Object> back = new LinkedHashMap<String, Object>();
		back.put("forward", forwardBack);
		back.put("backward", reverseBack);
		return back;
	}
	private Map<String, Object> buildBackwardHelper(final Set<String> path,
			Map<String,Object> back, final Map<String, Map<String, Set<String>>> completeTree,
			final Map<String, Set<String>> reverseTree, final String sourcePage, final String currentPage) {
		if(!back.containsKey("tree")){
			back.put("tree",new LinkedHashMap<String, Set<Map<String, Set<String>>>>());
		}
		if(!back.containsKey("path")){
			back.put("path",new LinkedHashSet<Set<String>>());
		}
		if(sourcePage.equals(currentPage)){
			if(!((Set<Set<String>>)back.get("path")).contains(path)){
				((Set<Set<String>>)back.get("path")).add(path);
			}
			return back;
		}
		Set<String> transitionSources = reverseTree.get(currentPage);		
		if (transitionSources != null) {
			for (final String transitionSource : transitionSources) {
				final Map<String, Set<String>> sourceTransitions = completeTree.get(transitionSource);
				final Map<String, Set<String>> filteredTransitions = new LinkedHashMap<String, Set<String>>();
				for (Map.Entry<String, Set<String>> transitionTarget : sourceTransitions.entrySet()) {
					if (transitionTarget.getKey().equals(currentPage)) {
						if(!sourcePage.equals(transitionTarget.getKey())){
							final Set<String> conditions = transitionTarget.getValue();
							final Set<String>cleanedConditions = new LinkedHashSet<String>();
							for(final String condition:conditions){
								String cleanedCondition = condition;
								cleanedCondition = cleanedCondition.replaceAll(" {2,}", "");
								cleanedConditions.add(cleanedCondition);
							}
							filteredTransitions.put(transitionTarget.getKey(),cleanedConditions);
						}
					}
				}
				List<Map<String, Set<String>>> transSet = null;
				if (((Map<String, Set<Map<String, Set<String>>>>)back.get("tree")).containsKey(transitionSource)){
					transSet = new ArrayList<Map<String, Set<String>>> (((Map<String, Set<Map<String, Set<String>>>>)back.get("tree")).get(transitionSource));
				}
				if (transSet == null){
					transSet = new ArrayList<Map<String, Set<String>>>();
				}
				Collections.reverse(transSet);
				transSet.add(filteredTransitions);
				Collections.reverse(transSet);
				final Set<String> newPath = new LinkedHashSet<String>();
				newPath.add(transitionSource);
				newPath.addAll(path);
				if(!transitionSource.equals(currentPage))buildBackwardHelper(newPath,back,completeTree,reverseTree,sourcePage,transitionSource);
				((Map<String, Set<Map<String, Set<String>>>>)back.get("tree")).put(transitionSource, new LinkedHashSet<Map<String, Set<String>>>(transSet));
			}
		}
		((Map<String, Set<Map<String, Set<String>>>>)back.get("tree")).put(currentPage, new LinkedHashSet<Map<String, Set<String>>>());
		return back;
	}
	public Map<String, Set<Map<String, String>>> getCompletePageTree(
			final File qmlFile) throws Exception {
		Map<String, Set<Map<String, String>>> treeMaps = retrievePageTreeInitOrdered(qmlFile);
		return treeMaps;
	}
	public Map<String, Set<Map<String, String>>> getCompleteTriggerTree(
			final File qmlFile) throws Exception {
		Map<String, Set<Map<String, String>>> triggerMaps = retrievePageTrigger(qmlFile);
		return triggerMaps;
	}
	public Map<String,Object> retrievePageTree(final String sourcePage, final String targetPage,
			final File qmlFile) throws Exception {
		Map<String, Object> treeMaps = retrievePageTreeInit(qmlFile);
		Map<String, Map<String, Set<String>>> completeTree = (Map<String, Map<String, Set<String>>>) treeMaps.get("forward");
		Map<String, Set<String>> reverseTree = (Map<String, Set<String>>) treeMaps.get("backward");
		final Set<String>  pathSet = new LinkedHashSet<String>();
		pathSet.add(targetPage);
		final Map<String, Object> back = buildBackwardHelper(pathSet,new LinkedHashMap<String, Object>(), completeTree, reverseTree, sourcePage, targetPage);
		return back;
	}
	public Map<String, ElementVector> retrieveGlobalInitials(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		final Map<String, ElementVector> back = new LinkedHashMap<String, ElementVector>();
		final NodeList variableNodes = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(),"variables/variable");
		final int count = variableNodes.getLength();
		final ElementVector initial = new ElementVector();
		for (int i = 0; i < count; ++i) {
			final Node variableNode = variableNodes.item(i);
			final NamedNodeMap nodeAttributes = variableNode.getAttributes();
			final Node nameAttribute = nodeAttributes.getNamedItem("name");
			final Node typeAttribute = nodeAttributes.getNamedItem("type");
			final String variable = nameAttribute.getNodeValue();
			final String type = typeAttribute.getNodeValue();
			final Set<Element> vector = new LinkedHashSet<Element>();
			if (type.equals("boolean")) {
				vector.add(new Element(true));
				vector.add(new Element(false));
				initial.getTupels().put(variable, vector);
				initial.getTupels().put(variable + ".value", vector);
			} else if (type.equals("string")) {
				vector.add(new Element(""));
				vector.add(new Element("''"));
				initial.getTupels().put(variable, vector);
				initial.getTupels().put(variable+ ".value", vector);
			}
			else if (type.equals("number")) {
				for (int a = -1; a <= 50; a++) {
					vector.add(new Element(Integer.getInteger(a+"")));
					vector.add(new Element("'"+a+"'"));
					vector.add(new Element(a+""));
				}
				initial.getTupels().put(variable, vector);
				initial.getTupels().put(variable+ ".value", vector);
			}
			else if (type.equals("singleChoiceAnswerOption")) {
				final Set<Element> uidVector = new LinkedHashSet<Element>();
				final Set<Element> valueVector = new LinkedHashSet<Element>();
				final Set<Element> labelVector = new LinkedHashSet<Element>();
				final NodeList questionList = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "descendant::*[@variable='"+variable+"']");
				final int questionCount = questionList.getLength();
				for (int j = 0; j < questionCount; ++j) {
					final Node node = questionList.item(j);
					NodeList optionsList = XmlClient.getInstance().getByXPathUncached(node, "descendant::answerOption");
					for (int k = 0; k < optionsList.getLength(); ++k) {
						final Node optionNode = (Node) optionsList.item(k);
						if (optionNode.getNodeName().equals("zofar:answerOption")) {
							final NamedNodeMap optionNodeAttributes = optionNode.getAttributes();
							final Node uidAttribute = optionNodeAttributes.getNamedItem("uid");
							final Node valueAttribute = optionNodeAttributes.getNamedItem("value");
							final Node labelAttribute = optionNodeAttributes.getNamedItem("label");
							String uid = null;
							if (uidAttribute != null)
								uid = uidAttribute.getTextContent();
							String value = null;
							if (valueAttribute != null)
								value = valueAttribute.getTextContent();
							String label = null;
							if (labelAttribute != null)
								label = labelAttribute.getTextContent();
							if (uid != null) {
								uidVector.add(new Element(uid));
								uidVector.add(new Element("'"+uid+"'"));
							}
							if (value != null) {
								valueVector.add(new Element(value,uid));
							}
							if (label != null)
								labelVector.add(new Element(label));
						} else
							System.out.println(optionNode.getNodeName());
					}
				}
				initial.getTupels().put(variable, valueVector);
				initial.getTupels().put(variable+ ".value", valueVector);
				initial.getTupels().put(variable+ "dropDown.value", valueVector);
				initial.getTupels().put(variable+ "missing.value", valueVector);
				initial.getTupels().put(variable+ ".valueId", uidVector);
				initial.getTupels().put(variable+ "dropDown.valueId", uidVector);
				initial.getTupels().put(variable+ "missing.valueId", uidVector);
			}
			else {
				System.out.println("unhandled variable ("+variable+") type "+type);
			}
		}
		back.put("GLOBAL", initial);
//		final NodeList pageList = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "page");
//			final NodeList questionList = XmlClient.getInstance().getByXPathUncached(pageNode, "descendant::*[@variable]");
//					NodeList optionsList = XmlClient.getInstance().getByXPathUncached(node, "descendant::answerOption");
//					final NodeList variableNodeList = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(),
		return back;
	}
	public Map<String, ElementVector> retrievePageInitials(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		final Map<String, ElementVector> back = new LinkedHashMap<String, ElementVector>();
		final NodeList pageList = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(), "page");
		final int pageCount = pageList.getLength();
		for (int i = 0; i < pageCount; ++i) {
			final Node pageNode = pageList.item(i);
			if (this.hasParent(pageNode, "zofar:researchdata"))
				continue;
			final NamedNodeMap pageAttributes = pageNode.getAttributes();
			final Node pageNameAttribute = pageAttributes.getNamedItem("uid");
			final String pageName = pageNameAttribute.getNodeValue();
			final ElementVector initial = new ElementVector();
			ElementVector global = null;
			if(back.containsKey("GLOBAL")) global = back.get("GLOBAL");
			if(global == null) global = new ElementVector();
			final NodeList questionList = XmlClient.getInstance().getByXPathUncached(pageNode, "descendant::*[@variable]");
			final int questionCount = questionList.getLength();
			for (int j = 0; j < questionCount; ++j) {
				final Node node = questionList.item(j);
				final String nodeType = node.getNodeName();
				final NamedNodeMap nodeAttributes = node.getAttributes();
				final Node variableAttribute = nodeAttributes.getNamedItem("variable");
				final String variable = variableAttribute.getNodeValue();
				if ((nodeType.equals("zofar:responseDomain")) || (nodeType.equals("zofar:left"))
						|| (nodeType.equals("zofar:right"))) {
					NodeList optionsList = XmlClient.getInstance().getByXPathUncached(node, "descendant::answerOption");
					final Set<Element> uidVector = new LinkedHashSet<Element>();
					final Set<Element> valueVector = new LinkedHashSet<Element>();
					final Set<Element> labelVector = new LinkedHashSet<Element>();
					for (int k = 0; k < optionsList.getLength(); ++k) {
						final Node optionNode = (Node) optionsList.item(k);
						if (optionNode.getNodeName().equals("zofar:answerOption")) {
							final NamedNodeMap optionNodeAttributes = optionNode.getAttributes();
							final Node uidAttribute = optionNodeAttributes.getNamedItem("uid");
							final Node valueAttribute = optionNodeAttributes.getNamedItem("value");
							final Node labelAttribute = optionNodeAttributes.getNamedItem("label");
							String uid = null;
							if (uidAttribute != null)
								uid = uidAttribute.getTextContent();
							String value = null;
							if (valueAttribute != null)
								value = valueAttribute.getTextContent();
							String label = null;
							if (labelAttribute != null)
								label = labelAttribute.getTextContent();
							if (uid != null)
								uidVector.add(new Element(uid));
							if (value != null)
								valueVector.add(new Element(value));
							if (label != null)
								labelVector.add(new Element(label));
						} else
							System.out.println(optionNode.getNodeName());
					}
					initial.getTupels().put(variable + "Dropdown.value", valueVector);
					initial.getTupels().put(variable + "Missing.value", valueVector);
					initial.getTupels().put(variable + ".value", valueVector);
				} else if (nodeType.equals("zofar:answerOption")|| nodeType.equals("zofar:SlotItem")) {
					final Set<Element> vector = new LinkedHashSet<Element>();
					vector.add(new Element(true));
					vector.add(new Element(false));
					initial.getTupels().put(variable + ".value", vector);
				} else if ((nodeType.equals("zofar:questionOpen")) || (nodeType.equals("zofar:question"))
						|| (nodeType.equals("zofar:attachedOpen"))) {
					final Node strTypeAttribute = nodeAttributes.getNamedItem("type");
					String strType = null;
					if (strTypeAttribute != null)
						strType = strTypeAttribute.getTextContent();
					if (strType == null) {
					} else {
						if (strType.equals("number")) {
							final Node minValueAttribute = nodeAttributes.getNamedItem("minValue");
							final Node maxValueAttribute = nodeAttributes.getNamedItem("maxValue");
							Integer minValue = null;
							if (minValueAttribute != null)
								minValue = Integer.getInteger(minValueAttribute.getTextContent());
							Integer maxValue = null;
							if (maxValueAttribute != null)
								maxValue = Integer.getInteger(maxValueAttribute.getTextContent());
							if ((minValue != null) && (maxValue != null)) {
								Set<Element> valueVector = new LinkedHashSet<Element>();
								for (int a = minValue.intValue(); a <= maxValue.intValue(); a++) {
									valueVector.add(new Element(new Integer(a)));
								}
								initial.getTupels().put(variable + ".value", valueVector);
								global.getTupels().put(variable, valueVector);
								global.getTupels().put(variable+ ".value", valueVector);
							}
							else{
								Set<Element> valueVector = new LinkedHashSet<Element>();
								for (int a = -1; a <= 5; a++) {
									valueVector.add(new Element(new Integer(a)));
								}
								initial.getTupels().put(variable, valueVector);
								initial.getTupels().put(variable + ".value", valueVector);
								global.getTupels().put(variable, valueVector);
								global.getTupels().put(variable+ ".value", valueVector);
							}
						} else if (strType.equals("grade")) {
							final Set<Element> valueVector = new LinkedHashSet<Element>();
							for (double a = 1.0D; a <= 6.0D; a = a + 0.1D) {
								valueVector.add(new Element(new Double(a)));
							}
							initial.getTupels().put(variable + ".value", valueVector);
							global.getTupels().put(variable, valueVector);
							global.getTupels().put(variable+ ".value", valueVector);
						} 
						else if (strType.equals("monthpicker")) {
						} 
						else if (strType.equals("text")) {
						}
					}
				} else if ((nodeType.equals("zofar:episodes")) || (nodeType.equals("zofar:episodesTable"))) {
				} else if (nodeType.equals("zofar:variable")) {
					final NodeList variableNodeList = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(),
							"/questionnaire/variables/variable[@name='" + variable + "']");
					if (variableNodeList != null) {
						int count = variableNodeList.getLength();
						for (int k = 0; k < count; ++k) {
							final Node variableNode = (Node) variableNodeList.item(k);
							final NamedNodeMap variableNodeAttributes = variableNode.getAttributes();
							final Node typeAttribute = variableNodeAttributes.getNamedItem("type");
							final String type = typeAttribute.getNodeValue();
							final Set<Element> vector = new LinkedHashSet<Element>();
							if (type.equals("boolean")) {
								vector.add(new Element(true));
								vector.add(new Element(false));
								initial.getTupels().put(variable + ".value", vector);
								global.getTupels().put(variable, vector);
								global.getTupels().put(variable+ ".value", vector);
							} else if (type.equals("string")) {
								initial.getTupels().put(variable, vector);
								global.getTupels().put(variable, vector);
								global.getTupels().put(variable+ ".value", vector);
							}
							else if (type.equals("number")) {
								for (int a = -1; a <= 5; a++) {
									vector.add(new Element(new Integer(a)));
								}
								initial.getTupels().put(variable, vector);
								initial.getTupels().put(variable+ ".value", vector);
								global.getTupels().put(variable, vector);
								global.getTupels().put(variable+ ".value", vector);
							}
						}
					} else {
						System.err.println("no variable declarations found for " + variable);
					}
				} else if (nodeType.equals("zofar:jsCheck")) {
					final Set<Element> vector = new LinkedHashSet<Element>();
					vector.add(new Element(true));
					vector.add(new Element(false));
					initial.getTupels().put(variable + ".value", vector);
					global.getTupels().put(variable, vector);
					global.getTupels().put(variable+ ".value", vector);
				}
				else {
				}
			}
			back.put("GLOBAL", global);
			back.put(pageName, initial);
		}
		return back;
	}
//			final NodeList usingNodes = XmlClient.getInstance().getByXPath(qmlFile.getAbsolutePath(),
//					final NodeList tmp = XmlClient.getInstance().getByXPath(qmlFile.getAbsolutePath(),
//							"//*[@variable='" + name + "']");
//							NodeList optionsList = XmlClient.getInstance().getByXPath(tmpNode,
//					final NodeList tmp = XmlClient.getInstance().getByXPath(qmlFile.getAbsolutePath(),
//							"//*[@variable='" + name + "']");
	public Map<String, ElementVector> generatePreloadInitials(final File qmlFile) throws Exception {
		if (qmlFile == null)
			throw new Exception("File is null");
		if (!qmlFile.canRead())
			throw new Exception("Cannot read File " + qmlFile.getAbsolutePath());
		Map<String, ElementVector> initialMap = new LinkedHashMap<String, ElementVector>();
		try {
			final NodeList usingNodes = XmlClient.getInstance().getByXPathUncached(qmlFile.getAbsolutePath(),
					"preloads/preload");
			final int count = usingNodes.getLength();
			for (int i = 0; i < count; ++i) {
				final Node preload = usingNodes.item(i);
				final NamedNodeMap nodeAttributes = preload.getAttributes();
				final Node nameAttribute = nodeAttributes.getNamedItem("name");
				final String name = nameAttribute.getNodeValue();
				final NodeList preloadItems = XmlClient.getInstance().getByXPathUncached(preload, "preloadItem");
				final int itemCount = usingNodes.getLength();
				if (itemCount > 0) {
					final ElementVector initial = new ElementVector();
					for (int j = 0; j < itemCount; ++j) {
						final Node preloadItem = preloadItems.item(j);
						final NamedNodeMap itemAttributes = preloadItem.getAttributes();
						final Node variableAttribute = itemAttributes.getNamedItem("variable");
						final String variable = variableAttribute.getNodeValue();
						final Node valueAttribute = itemAttributes.getNamedItem("value");
						final String value = valueAttribute.getNodeValue();
						Set<Element> valueVector = new LinkedHashSet<Element>();
						valueVector.add(new Element(value));
						initial.getTupels().put(variable + ".value", valueVector);
					}
					initialMap.put(name, initial);
				} else {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return initialMap;
	}
}
