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
package de.his.zofar.generator.maven.plugin.generator.page;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.text.TextType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.AbstractLabeledAnswerOptionType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.SortableContainerType;
import de.his.zofar.xml.questionnaire.TextInstructionType;
import de.his.zofar.xml.questionnaire.TextIntroductionType;
import de.his.zofar.xml.questionnaire.TextParagraphType;
import de.his.zofar.xml.questionnaire.TextQuestionType;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
import de.his.zofar.xml.questionnaire.TextTitleType;
import eu.dzhw.zofar.management.utils.objects.CollectionClient;
import eu.dzhw.zofar.management.utils.string.ReplaceClient;
/**
 * @author le
 * 
 */
abstract class AbstractXhtmlElementCreator {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String EL_PATTERN = "#{%s}";
	private static final String DEVEL_PATTERN = "#{devProxy.analyze('bla','%s','debug')}";
	protected static final String UID_DELIMITER = ":";
	protected static final String SELECT_PATH_IN_ORDER = "./*";
	protected static final String PIPE_CLASSES = "zo-pipes";
	/**
	 * the currently set variable name. used in addLabelFacetToAnswerOption().
	 * must be set whenever a variable is referenced in a response domain.
	 * variable is set to be able to connect labels to a variable.
	 */
	private String variableName;
	final ReplaceClient replacer = ReplaceClient.getInstance();
	/**
	 * generates the unique UID with all UIDs of all
	 * {@link IdentificationalType} parents.
	 * 
	 * @param identificational
	 * @return
	 * @throws XmlException
	 */
	protected String generateUid(final IdentificationalType identificational) throws XmlException {
		final String uidAttribute = "uid";
		final StringBuilder completeUid = new StringBuilder(identificational.getUid().trim());
		Node parentNode = identificational.getDomNode().getParentNode();
		while (parentNode != null) {
			if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasAttributes()) {
				final Node uidNode = parentNode.getAttributes().getNamedItem(uidAttribute);
				if (uidNode != null) {
					final String parentUid = uidNode.getNodeValue().trim();
					completeUid.insert(0, parentUid + UID_DELIMITER);
				}
			}
			parentNode = parentNode.getParentNode();
		}
		return completeUid.toString();
	}
	protected void setIdentifier(final IdentificationalType source, final com.sun.java.jsf.composite.common.IdentificationalType target) throws XmlException {
		target.setId(source.getUid().trim());
		enhanceExpressions(source,target);
	}
	private void enhanceExpressions(final IdentificationalType source, final com.sun.java.jsf.composite.common.IdentificationalType target) {
		if(PageManager.getInstance().getMojo().isOverrideRendering()){
			enhanceExpressions(source,"visible",target);
		}
		if(PageManager.getInstance().getMojo().isMdm()){
		}
	}
	private void enhanceExpressions(final IdentificationalType source,final String mode, final com.sun.java.jsf.composite.common.IdentificationalType target) {
		try {
			final String capitalized = WordUtils.capitalize(mode);
			final Method getMethod = source.getClass().getMethod("get"+capitalized, new Class<?>[0]);
			final Method setMethod = source.getClass().getMethod("set"+capitalized, String.class);
			final String value = (String) getMethod.invoke(source, new Object[0]);
			if(value != null){
				Object[] args = new Object[1];
				args[0] = "devProxy.analyze('"+generateTargetId(target)+"','"+org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(value)+"','"+mode.toUpperCase()+"',true)";
				setMethod.invoke(source, args);
			}
		} catch (NoSuchMethodException | SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		} catch (XmlException e) {
			e.printStackTrace();
		}
	}
	protected String generateTargetId(final com.sun.java.jsf.composite.common.IdentificationalType target) throws XmlException {
		final String idAttribute = "id";
		final StringBuilder completeUid = new StringBuilder(target.getId().trim());
		Node parentNode = target.getDomNode().getParentNode();
		while (parentNode != null) {
			if (parentNode.getNodeType() == Node.ELEMENT_NODE && parentNode.hasAttributes()) {
				final Node uidNode = parentNode.getAttributes().getNamedItem(idAttribute);
				if (uidNode != null) {
					final String parentUid = uidNode.getNodeValue().trim();
					completeUid.insert(0, parentUid + UID_DELIMITER);
				}
			}
			parentNode = parentNode.getParentNode();
		}
		return completeUid.toString();
	}
	/**
	 * creates an EL Expression to reference the backing bean of the variable.
	 * 
	 * @param variableName
	 * @return
	 */
	protected String createVariableReference(final String variableName) {
		return createElExpression(variableName);
	}
	/**
	 * creates an EL Expression.
	 * 
	 * @param expression
	 * @return
	 */
	protected String createElExpression(final String expression) {
		return String.format(EL_PATTERN, expression);
	}
	/**
	 * adds text to all kind of facets. the caller is responsible that only
	 * valid text types are added to the facet.
	 * 
	 * @param source
	 *            the text to be added
	 * @param targetFacet
	 *            the facet
	 * @throws XmlException
	 */
	protected String addTextToFacetAnswerOption(final de.his.zofar.xml.questionnaire.TextType source, final FacetType targetFacet) throws XmlException {
		TextType xhtmlText = null;
		final Class<? extends de.his.zofar.xml.questionnaire.TextType> sourceClass = source.getClass();
		if (TextIntroductionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewIntro();
		} else if (TextInstructionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewInstruction();
		} else if (TextTitleType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewTitle();
		} else if (TextParagraphType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewText();
		} else if (TextQuestionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewQuestion();
		} else if (TextResponseOptionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewResponseOption();
		} else {
			xhtmlText = targetFacet.addNewText();
		}
		return createTextAnswerOption(source, xhtmlText);
	}
	protected void addTextToFacet(final de.his.zofar.xml.questionnaire.TextType source, final FacetType targetFacet) throws XmlException {
		this.addTextToFacet(source, targetFacet, true);
	}
	protected void addTextToFacet(final de.his.zofar.xml.questionnaire.TextType source, final FacetType targetFacet, final boolean container) throws XmlException {
		TextType xhtmlText = null;
		final Class<? extends de.his.zofar.xml.questionnaire.TextType> sourceClass = source.getClass();
		if (TextIntroductionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewIntro();
		} else if (TextInstructionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewInstruction();
		} else if (TextTitleType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewTitle();
		} else if (TextParagraphType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewText();
		} else if (TextQuestionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewQuestion();
		} else if (TextResponseOptionType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetFacet.addNewResponseOption();
		} else {
			xhtmlText = targetFacet.addNewText();
		}
		xhtmlText.setContainer(container);
		createText(source, xhtmlText);
	}
	protected void addTitleToFacet(final de.his.zofar.xml.questionnaire.TextType source, final FacetType targetFacet) throws XmlException {
		TextType xhtmlText = null;
		xhtmlText = targetFacet.addNewText();
		createText(source, xhtmlText);
	}
	protected void addTextToComparsionUnit(final de.his.zofar.xml.questionnaire.TextType source, final com.sun.java.jsf.composite.composite.ComparisonUnitType targetUnit) throws XmlException {
		TextType xhtmlText = null;
		final Class<? extends de.his.zofar.xml.questionnaire.TextType> sourceClass = source.getClass();
		if (de.his.zofar.xml.questionnaire.TextType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetUnit.addNewText();
		}
		if (xhtmlText != null)
			createText(source, xhtmlText);
	}
	protected void addTextToSort(final de.his.zofar.xml.questionnaire.TextType source, final com.sun.java.jsf.composite.common.SortType targetSort) throws XmlException {
		TextType xhtmlText = null;
		final Class<? extends de.his.zofar.xml.questionnaire.TextType> sourceClass = source.getClass();
		if (de.his.zofar.xml.questionnaire.TextType.class.isAssignableFrom(sourceClass)) {
			xhtmlText = targetSort.addNewText();
		}
		if (xhtmlText != null)
			createText(source, xhtmlText);
	}
	private String createTextAnswerOption(final de.his.zofar.xml.questionnaire.TextType source, final TextType target) throws XmlException {
		setIdentifier(source, target);
		final String text = source.newCursor().getTextValue();
		String key = null;
		if (PageManager.getInstance().getMojo() != null) {
			key = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), text);
		}
		final String textValue = (key == null) ? text : createElExpression(key);
		target.newCursor().setTextValue(textValue);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		return key;
	}
	protected void createTextHelper(int lft, Node node, final de.his.zofar.xml.questionnaire.TextType source, final TextType target) throws XmlException {
		if (node == null)
			return;
		final String nodeName = node.getNodeName();
		if (nodeName.equals("#text")) {
			String text = node.getNodeValue();
			String key = null;
			if (PageManager.getInstance().getMojo() != null) {
				key = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source) + "_" + lft, text);
			}
			final String textValue = (key == null) ? text : createElExpression(key);
			XmlCursor cursor = target.newCursor();
			cursor.toEndToken();
			cursor.insertChars(textValue);
		} else if (nodeName.equals("zofar:jumper")) {
			com.sun.java.jsf.composite.common.JumperType jumper = target.addNewJumper();
			NamedNodeMap attributes = node.getAttributes();
			if (attributes != null) {
				Node valueNode = attributes.getNamedItem("value");
				if (valueNode != null) {
					String text = valueNode.getNodeValue();
					String key = null;
					if (PageManager.getInstance().getMojo() != null) {
						key = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source) + "_" + lft, text);
					}
					final String textValue = (key == null) ? text : createElExpression(key);
					jumper.setValue(textValue);
				}
				Node targetNode = attributes.getNamedItem("target");
				if (targetNode != null) {
					jumper.setTargetPage(targetNode.getNodeValue());
				}
				if (jumper.isSetDisabled()) {
					Node disabledNode = attributes.getNamedItem("disabled");
					if (disabledNode != null) {
						jumper.setDisabled(Boolean.parseBoolean(disabledNode.getNodeValue()));
					}
				}
				if (jumper.isSetVisible()) {
					Node visibleNode = attributes.getNamedItem("visible");
					if (visibleNode != null) {
						jumper.setVisible(visibleNode.getNodeValue());
					}
				}
			}
		} else {
			NodeList childs = node.getChildNodes();
			final int count = childs.getLength();
			for (int a = 0; a < count; a++) {
				final Node child = childs.item(a);
				createTextHelper(lft + a, child, source, target);
			}
		}
	}
	private void createText(final de.his.zofar.xml.questionnaire.TextType source, final TextType target) throws XmlException {
		setIdentifier(source, target);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		Node node = source.getDomNode();
		createTextHelper(0, node, source, target);
	}
	/**
	 * create a XML {@link de.his.zofar.xml.questionnaire.TextType} from String.
	 * 
	 * @param parent
	 * @param uid
	 * @param text
	 * @param textType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends de.his.zofar.xml.questionnaire.TextType> T createXmlTextTypeFromString(final AbstractLabeledAnswerOptionType parent, final String uid, final String text, final Class<T> textType) {
		final T newText = (T) parent.addNewLabel();
		newText.setUid(uid);
		newText.newCursor().setTextValue(text);
		return newText;
	}
	/**
	 * creates a sort container if necessary. returns NULL if the source has no
	 * sort condition.
	 * 
	 * @param source
	 * @param target
	 * @return
	 * @throws XmlException
	 */
	protected SortType createSortContainer(final SortableContainerType source, final SortType target) throws XmlException {
		if (!source.isSetSortCondition() && !source.isSetSortMode()) {
			return null;
		}
		target.setId("sort");
		enhanceExpressions(source,"SortCondition",target);
		target.setSorted(createElExpression(source.getSortCondition()));
		target.setBean("#{sortBean}");
		String modeStr = "UNKOWN";
		if (source.isSetSortMode()) {
			SortType.Mode.Enum mode = SortType.Mode.RANDOM;
			switch (source.getSortMode().intValue()) {
			case SortableContainerType.SortMode.INT_RANDOM:
				mode = SortType.Mode.RANDOM;
				break;
			case SortableContainerType.SortMode.INT_RANDOMONCE:
				mode = SortType.Mode.RANDOMONCE;
				break;
			default:
				throw new RuntimeException("sort mode " + source.getSortMode() + " is either not yet implemened or not supported");
			}
			target.setMode(mode);
			modeStr = mode.toString();
		}
		return target;
	}
	/**
	 * @param variableName
	 */
	protected void setVariableName(final String variableName) {
		this.variableName = variableName;
	}
	/**
	 * @return
	 */
	protected String getVariableName() {
		return variableName;
	}
}
