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
/**
 *
 */
package de.his.zofar.generator.maven.plugin.generator.variable;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.schema.beans.BeanDocument.Bean;
import org.springframework.schema.beans.BeansDocument;
import org.springframework.schema.beans.BeansDocument.Beans;
import org.springframework.schema.beans.BeansDocument.Beans.DefaultAutowire;
import org.springframework.schema.beans.ConstructorArgDocument.ConstructorArg;
import org.springframework.schema.beans.EntryType;
import org.springframework.schema.beans.PropertyType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import de.his.zofar.generator.maven.plugin.generator.AbstractGenerator;
import de.his.zofar.generator.maven.plugin.generator.page.PageManager;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionnaireDocument;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
import de.his.zofar.xml.questionnaire.VariableType;
import eu.dzhw.zofar.management.utils.xml.XmlClient;
/**
 * this class creates the survey-variable-context.xml file which consists of all
 * variables that are used in the survey.
 * 
 * @author le
 * 
 */
public class VariableGenerator extends AbstractGenerator<BeansDocument> {
	private static final String FILE_NAME = "survey-variable-context.xml";
	private static final String SESSION_CONTROLLER_NAME = "sessionController";
	private static final String SESSION_CONTROLLER_CLASS = "de.his.zofar.presentation.surveyengine.controller.SessionController";
	private static final String DEVPROXY_NAME = "devProxy";
	private static final String DEVPROXY_CLASS = "de.his.zofar.presentation.surveyengine.dev.DevProxy";
	private static final String NUMBER_VARIABLE_BEAN = "de.his.zofar.presentation.surveyengine.NumberValueTypeBean";
	private static final String BOOLEAN_VARIABLE_BEAN = "de.his.zofar.presentation.surveyengine.BooleanValueTypeBean";
	private static final String STRING_VARIABLE_BEAN = "de.his.zofar.presentation.surveyengine.StringValueTypeBean";
	private static final String SINGLECHOICEANSWEROPTION_BEAN = "de.his.zofar.presentation.surveyengine.SingleChoiceAnswerOptionTypeBean";
	private static final String SINGLECHOICEANSWEROPTION_VALUES_NAME = "optionValues";
	private static final String SINGLECHOICEANSWEROPTION_MISSINGS_NAME = "missingValues";
	private static final String SINGLECHOICEANSWEROPTION_LABELS_NAME = "labels";
	private static final String DEFAULT_VARIABLE_BEAN_SCOPE = "request";
	private final Map<String, Bean> variableCache;
	/**
	 * implement singleton pattern.
	 */
	public VariableGenerator() {
		super(BeansDocument.Factory.newInstance());
		variableCache = new HashMap<String, Bean>();
	}
	/**
	 * creates the survey-variable-context.xml with mandatory content.
	 * 
	 * @return the created document
	 */
	public final void createDocument() {
		final Beans beans = this.getDocument().addNewBeans();
		final XmlCursor cursor = this.getDocument().newCursor();
		if (cursor.toFirstChild()) {
			cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"),
					"http://www.springframework.org/schema/beans "
							+ "http://www.springframework.org/schema/beans/spring-beans-3.2.xsd");
		}
		beans.setDefaultAutowire(DefaultAutowire.BY_NAME);
		if (PageManager.getInstance().getMojo().isOverrideRendering()) {
			final Bean devProxy = beans.addNewBean();
			devProxy.setName(DEVPROXY_NAME);
			devProxy.setClass1(DEVPROXY_CLASS);
			devProxy.setScope("request");
		}
		final Bean sessionController = beans.addNewBean();
		sessionController.setName(SESSION_CONTROLLER_NAME);
		sessionController.setClass1(SESSION_CONTROLLER_CLASS);
		sessionController.setScope("session");
	}
	/**
	 * saves the document in the path as survey-variable-context.xml.
	 * 
	 * @param path
	 *            the path in which the document will be saved. file name is
	 *            'survey-variable-context.xml'
	 * @throws IOException
	 */
	public final void saveDocument(final String path) throws IOException {
		final XmlOptions options = new XmlOptions();
		options.setSavePrettyPrint();
		options.setUseDefaultNamespace();
		if (!this.validate()) {
			throw new IllegalStateException("the document ist not a valid XML document. File: " + FILE_NAME);
		}
		this.saveDocument(path, FILE_NAME, options);
	}
	/**
	 * @param variableName
	 */
	public final void addNumberVariable(final VariableType variable) {
		this.addVariableBean(variable, NUMBER_VARIABLE_BEAN, DEFAULT_VARIABLE_BEAN_SCOPE);
	}
	/**
	 * adds a bean definition for a boolean variable.
	 * 
	 * @param variableName
	 *            the name of the boolean variable
	 * @param itemUid
	 *            the UID of the answer option
	 */
	public final void addBooleanVariable(final VariableType variable, final String itemUid) {
		final Bean bean = this.addVariableBean(variable, BOOLEAN_VARIABLE_BEAN, DEFAULT_VARIABLE_BEAN_SCOPE);
		final ConstructorArg arg = bean.addNewConstructorArg();
		arg.setValue2(itemUid);
	}
	/**
	 * adds a bean definition for a string variable.
	 * 
	 * @param variableName
	 *            the name of the string variable
	 */
	public final void addStringVariable(final VariableType variable) {
		this.addVariableBean(variable, STRING_VARIABLE_BEAN, DEFAULT_VARIABLE_BEAN_SCOPE);
	}
	/**
	 * adds single choice answer options for a variable.
	 * 
	 * @param variableName
	 *            the name of the string variable
	 * @param properties
	 */
	private final void addSingleChoiceAnswerOption(final VariableType variable, final PropertyType... properties) {
		this.addVariableBean(variable, SINGLECHOICEANSWEROPTION_BEAN, DEFAULT_VARIABLE_BEAN_SCOPE, properties);
	}
	public final void addSingleChoiceAnswerOption(final VariableType variable, final QuestionnaireDocument source,final Map<String,Set<XmlObject>> rdcCache) {
		addSingleChoiceAnswerOption(variable, source, null,rdcCache);
	}
	/**
	 * @param variableName
	 * @param source
	 */
	public final void addSingleChoiceAnswerOption(final VariableType variable, final QuestionnaireDocument source,
			final Map<String, String> options,final Map<String,Set<XmlObject>> rdcCache) {
		Map<String, Map<String, String>> data = parseSCData(variable.getName(), source,	rdcCache);
		if (options != null) {
			this.addSingleChoiceAnswerOption(variable, this.createSingleChoiceOptionValues(data.get("values")),
					this.createSingleChoiceOptionMissings(data.get("missings")),
					this.createSingleChoiceOptionLabels(data.get("labels")));
		} else
			this.addSingleChoiceAnswerOption(variable, this.createSingleChoiceOptionValues(data.get("values")),
					this.createSingleChoiceOptionMissings(data.get("missings")));
	}
	/**
	 * creates the option values map for the single choice answer option bean.
	 * 
	 * @param valuesMap
	 *            a map of all values, which are used for e.g. response insertion.
	 * @return the created property type
	 */
	public final PropertyType createSingleChoiceOptionValues(final Map<String, String> valuesMap) {
		return this.createPropertyTypeMap(SINGLECHOICEANSWEROPTION_VALUES_NAME, valuesMap);
	}
	/**
	 * creates the option values map for the single choice answer option bean.
	 * 
	 * @param valuesMap
	 *            a map of all values, which are used for e.g. response insertion.
	 * @return the created property type
	 */
	public final PropertyType createSingleChoiceOptionMissings(final Map<String, String> valuesMap) {
		return this.createPropertyTypeMap(SINGLECHOICEANSWEROPTION_MISSINGS_NAME, valuesMap);
	}
	/**
	 * creates the option values map for the single choice answer option bean.
	 * 
	 * @param valuesMap
	 *            a map of all values, which are used for e.g. response insertion.
	 * @return the created property type
	 */
	public final PropertyType createSingleChoiceOptionLabels(final Map<String, String> valuesMap) {
		return this.createPropertyTypeMap(SINGLECHOICEANSWEROPTION_LABELS_NAME, valuesMap);
	}
	/**
	 * @param propertyName
	 * @param entries
	 * @return
	 */
	private PropertyType createPropertyTypeMap(final String propertyName, final Map<String, String> entries) {
		final PropertyType property = PropertyType.Factory.newInstance();
		property.setName(propertyName);
		final org.springframework.schema.beans.MapDocument.Map map = property.addNewMap();
		for (final Entry<String, String> entry : entries.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			final EntryType entryType = map.addNewEntry();
			entryType.setKey2(key);
			entryType.setValue2(value);
		}
		return property;
	}
	/**
	 * @param variableName
	 * @param className
	 * @param scope
	 * @param propertyArray
	 * @return bean
	 */
	private Bean addVariableBean(final VariableType variable, final String className, final String scope,
			final PropertyType... propertyArray) {
		if (variableCache.containsKey(variable.getName()))
			return variableCache.get(variable.getName());
		final Bean bean = this.getDocument().getBeans().addNewBean();
		bean.setName(variable.getName());
		bean.setClass1(className);
		bean.setScope(scope);
		final ConstructorArg sessionController = bean.addNewConstructorArg();
		sessionController.setRef2(SESSION_CONTROLLER_NAME);
		final ConstructorArg value = bean.addNewConstructorArg();
		value.setValue2(variable.getName());
		PropertyType[] properties = propertyArray;
		if (variable.isSetAlternativeText()) {
			final PropertyType property = PropertyType.Factory.newInstance();
			property.setName("alternative");
			final String key = PageManager.getInstance().getMojo().addTextToBundle(variable.getName() + ".alternative",
					variable.getAlternativeText());
			property.setValue2(key);
			PageManager.getInstance().getMojo().writeLabelInformation(variable.getName() + ".alternative",
					variable.getName(), "true", key);
			properties = ArrayUtils.add(properties, property);
		}
		if (properties != null) {
			bean.setPropertyArray(properties);
		}
		variableCache.put(variable.getName(), bean);
		return bean;
	}
	private Map<String, Map<String, String>> parseSCData(final String variableName,
			final QuestionnaireDocument source) {
		final Instant time1 = Instant.now();
		final Map<String, String> values = new LinkedHashMap<String, String>();
		final Map<String, String> missings = new LinkedHashMap<String, String>();
		final Map<String, String> labels = new LinkedHashMap<String, String>();
		final Instant time2 = Instant.now();
		String cleanedVariableName = variableName.replaceAll("dropDown", "");
		cleanedVariableName = cleanedVariableName.replaceAll("missing", "");
		final Instant time3 = Instant.now();
		//final XmlObject[] rdcs = this.getByXPath(source.getQuestionnaire(), "//*[@variable='"+cleanedVariableName+"']");
		final XmlObject[] rdcs = XmlClient.getInstance().getByXPathUncached(source.getQuestionnaire(), "//*[@variable='"+cleanedVariableName+"']");
		final Instant time4 = Instant.now();
		final Set<XmlObject> answers = new LinkedHashSet<XmlObject>();
		for(final XmlObject rdc : rdcs) {  	
			//final XmlObject[] tmp = this.getByXPath(rdc, "descendant::zofar:answerOption");
			final XmlObject[] tmp = XmlClient.getInstance().getByXPathUncached(rdc, "descendant::zofar:answerOption");
			for(final XmlObject answer : tmp) { 
				answers.add(answer);
			}
		}
		final Instant time5 = Instant.now();
		long duration1 = Duration.between(time1, time2).toMillis();
		long duration2 = Duration.between(time2, time3).toMillis();
		long duration3 = Duration.between(time3, time4).toMillis();
		long duration4 = Duration.between(time4, time5).toMillis();
		for (final XmlObject answer : answers) {
			if ((QuestionSingleChoiceAnswerOptionType.class).isAssignableFrom(answer.getClass())) {
				final QuestionSingleChoiceAnswerOptionType option = (QuestionSingleChoiceAnswerOptionType) answer;
				if (option.isSetMissing() && option.getMissing()) {
					missings.put(option.getUid(), option.getValue());
				}
				else {
					values.put(option.getUid(), option.getValue());
				}
				final StringBuffer labelBuffer = new StringBuffer();
				if (option.getLabelArray().length > 0) {
					for (final TextResponseOptionType label : option.getLabelArray()) {
						labelBuffer.append(label.newCursor().getTextValue());
					}
				}
				final String unfiltered = labelBuffer.toString();
				final String filtered = unfiltered.replaceAll("#\\{[^\\}]*\\}", "");
				labels.put(option.getUid(), filtered);
			} 
		}
		final Map<String, Map<String, String>> back = new HashMap<String, Map<String, String>>();
		back.put("values", values);
		back.put("missings", missings);
		back.put("labels", labels);
		return back;
	}
	private Map<String, Map<String, String>> parseSCData(final String variableName,
			final QuestionnaireDocument source,final Map<String,Set<XmlObject>> rdcCache) {
		final Instant time1 = Instant.now();
		final Map<String, String> values = new LinkedHashMap<String, String>();
		final Map<String, String> missings = new LinkedHashMap<String, String>();
		final Map<String, String> labels = new LinkedHashMap<String, String>();
		final Instant time2 = Instant.now();
		String cleanedVariableName = variableName.replaceAll("dropDown", "");
		cleanedVariableName = cleanedVariableName.replaceAll("missing", "");
		final Instant time3 = Instant.now();
		//final XmlObject[] rdcs = this.getByXPath(source.getQuestionnaire(), "//*[@variable='"+cleanedVariableName+"']");
		//final XmlObject[] rdcs = XmlClient.getInstance().getByXPath(source.getQuestionnaire(), "//*[@variable='"+cleanedVariableName+"']");
		final Set<XmlObject> rdcs = rdcCache.get(cleanedVariableName);
		final Instant time4 = Instant.now();
		final Set<XmlObject> answers = new LinkedHashSet<XmlObject>();
		if(rdcs != null) {
			for(final XmlObject rdc : rdcs) {  	
				//final XmlObject[] tmp = this.getByXPath(rdc, "descendant::zofar:answerOption");
				final XmlObject[] tmp = XmlClient.getInstance().getByXPathUncached(rdc, "descendant::zofar:answerOption");
				for(final XmlObject answer : tmp) { 
					answers.add(answer);
				}
			}
		}
		final Instant time5 = Instant.now();
		for (final XmlObject answer : answers) {
			if ((QuestionSingleChoiceAnswerOptionType.class).isAssignableFrom(answer.getClass())) {
				final QuestionSingleChoiceAnswerOptionType option = (QuestionSingleChoiceAnswerOptionType) answer;
				if (option.isSetMissing() && option.getMissing()) {
					missings.put(option.getUid(), option.getValue());
				}
				else {
					values.put(option.getUid(), option.getValue());
				}
				final StringBuffer labelBuffer = new StringBuffer();
				if (option.getLabelArray().length > 0) {
					for (final TextResponseOptionType label : option.getLabelArray()) {
						labelBuffer.append(label.newCursor().getTextValue());
					}
				}
				final String unfiltered = labelBuffer.toString();
				final String filtered = unfiltered.replaceAll("#\\{[^\\}]*\\}", "");
				labels.put(option.getUid(), filtered);
			} 
		}
		final Map<String, Map<String, String>> back = new HashMap<String, Map<String, String>>();
		back.put("values", values);
		back.put("missings", missings);
		back.put("labels", labels);
		return back;
	}
//		final String query = "declare namespace zofar='http://www####/zofar/xml/questionnaire' //zofar:responseDomain[@variable='"
	public String findItemUidOfBoolean(final String variableName, final QuestionnaireDocument source,final Map<String,Set<XmlObject>> rdcCache) {
		String itemUid = null;
		final Set<XmlObject> usingNodes = rdcCache.get(variableName); 
		if (usingNodes != null) {
			for (final XmlObject usingNode : usingNodes) {
				final Node e = usingNode.getDomNode();
				final NamedNodeMap nodeAttributes = e.getAttributes();
				final Node uidNode = nodeAttributes.getNamedItem("uid");
				if(uidNode != null) {
					final String declaredUID = uidNode.getNodeValue();
					itemUid = declaredUID;
					break;
				}
			}
		}
		return itemUid;
	}
	public String findItemUidOfBoolean(final String variableName, final QuestionnaireDocument source) {
		String itemUid = null;
		final XmlObject[] usingNodes = XmlClient.getInstance().getByXPathUncached(source,
				"//*[@variable='" + variableName + "']");
		if (usingNodes != null) {
			for (final XmlObject usingNode : usingNodes) {
				final Node e = usingNode.getDomNode();
				final NamedNodeMap nodeAttributes = e.getAttributes();
				final Node uidNode = nodeAttributes.getNamedItem("uid");
				if(uidNode != null) {
					final String declaredUID = uidNode.getNodeValue();
					itemUid = declaredUID;
					break;
				}
			}
		}
		return itemUid;
	}
//	public XmlObject[] getByXPath(final XmlObject root, final String query) {
}
