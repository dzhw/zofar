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
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.answer.AttachedGradeQuestionType;
import com.sun.java.jsf.composite.answer.AttachedMailQuestionType;
import com.sun.java.jsf.composite.answer.AttachedNumberQuestionType;
import com.sun.java.jsf.composite.answer.AttachedOpenQuestionType;
import com.sun.java.jsf.composite.answer.BaseOptionType;
import com.sun.java.jsf.composite.answer.SmallOpenOptionType;
import com.sun.java.jsf.composite.question.BaseQuestionType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.AbstractLabeledAnswerOptionType;
import de.his.zofar.xml.questionnaire.AttachedQuestionOpenType;
import de.his.zofar.xml.questionnaire.QuestionHeaderType;
import de.his.zofar.xml.questionnaire.QuestionOpenPostfixType;
import de.his.zofar.xml.questionnaire.QuestionOpenPrefixType;
import de.his.zofar.xml.questionnaire.TextInstructionType;
import de.his.zofar.xml.questionnaire.TextIntroductionType;
import de.his.zofar.xml.questionnaire.TextQuestionType;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
/**
 * class bundles all common methods to create questions.
 * 
 * @author le
 * 
 */
abstract class AbstractXhtmlQuestionCreator extends AbstractXhtmlElementCreator {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	/**
	 * adds a question header.
	 * 
	 * @param source
	 * @param targetQuestion
	 * @throws XmlException
	 */
	protected void addQuestionHeader(final QuestionHeaderType source, final BaseQuestionType targetQuestion) throws XmlException {
		final FacetType headerFacet = targetQuestion.addNewFacet();
		headerFacet.setName(FacetType.Name.HEADER);
		if (source == null)
			return;
		final XmlObject[] children = source.selectPath(SELECT_PATH_IN_ORDER);
		for (final XmlObject child : children) {
			if (de.his.zofar.xml.questionnaire.TextType.class.isAssignableFrom(child.getClass())) {
				addTextToQuestionHeader((de.his.zofar.xml.questionnaire.TextType) child, headerFacet);
			}
		}
	}
	/**
	 * adds an attached open question to a question item.
	 * 
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	protected void addAttachedOpenQuestion(final AttachedQuestionOpenType source, final AttachedOpenQuestionType target) throws XmlException {
		setIdentifier(source, target);
		target.setVar(createVariableReference(source.getVariable()));
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		if (source.isSetPrefix()) {
			final QuestionOpenPrefixType prefix = source.getPrefix();
			final FacetType prefixFacet = target.addNewFacet();
			prefixFacet.setName(FacetType.Name.PREFIX);
			for (final TextResponseOptionType label : prefix.getLabelArray()) {
				addTextToFacet(label, prefixFacet);
			}
		}
		if (source.isSetPostfix()) {
			final QuestionOpenPostfixType postfix = source.getPostfix();
			final FacetType postfixFacet = target.addNewFacet();
			postfixFacet.setName(FacetType.Name.POSTFIX);
			for (final TextResponseOptionType label : postfix.getLabelArray()) {
				addTextToFacet(label, postfixFacet);
			}
		}
		if (source.isSetMaxLength()) {
			target.setMaxlength(source.getMaxLength());
		}
		if (source.isSetSize()) {
			target.setSize(source.getSize().intValue());
		}
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
	}
	/**
	 * adds text to question headers.
	 * 
	 * @param source
	 * @param targetFacet
	 * @throws XmlException
	 */
	protected void addTextToQuestionHeader(final de.his.zofar.xml.questionnaire.TextType source, final FacetType targetFacet) throws XmlException {
		final Class<?> textClass = source.getClass();
		if (!TextQuestionType.class.isAssignableFrom(textClass) && !TextInstructionType.class.isAssignableFrom(textClass) && !TextIntroductionType.class.isAssignableFrom(textClass)) {
			throw new IllegalStateException("text of type " + source.getClass().getSimpleName() + " is not allowed in question header.");
		}
		addTextToFacet(source, targetFacet);
	}
	/**
	 * adds abstract labeled answer option type, like single choice options and
	 * multiple choice items.
	 * 
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	protected void addAbstractAnswerOption(final AbstractLabeledAnswerOptionType source, final BaseOptionType target) throws XmlException {
		addLabelFacetToAnswerOption(source, target);
		setIdentifier(source, target);
		target.setMissing(source.getMissing());
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		final AttachedQuestionOpenType[] attachedOpensSource = source.getQuestionOpenArray();
		if ((attachedOpensSource != null) && (attachedOpensSource.length > 0)) {
			final int count = attachedOpensSource.length;
			for (int a = 0; a < count; a++) {
				final AttachedQuestionOpenType attachedOpenSource = attachedOpensSource[a];
				final String type = attachedOpenSource.getType();
				if (type.equals("text")) {
					AttachedOpenQuestionType attached = target.addNewAttachedOpenQuestion();
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				} 
				else if (type.equals("nonumbers")) {
					AttachedOpenQuestionType attached = target.addNewAttachedNoNumbersQuestion();
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				}
				else if (type.equals("number")) {
					AttachedNumberQuestionType attached = target.addNewAttachedNumberQuestion();
					if ((attachedOpenSource).isSetMinValue())
						attached.setMinValue(attachedOpenSource.getMinValue());
					if (attachedOpenSource.isSetMaxValue())
						attached.setMaxValue(attachedOpenSource.getMaxValue());
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				} else if (type.equals("grade")) {
					AttachedGradeQuestionType attached = target.addNewAttachedGradeQuestion();
					if ((attachedOpenSource).isSetMinValue())
						attached.setMinValue(attachedOpenSource.getMinValue());
					if (attachedOpenSource.isSetMaxValue())
						attached.setMaxValue(attachedOpenSource.getMaxValue());
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				} else if (type.equals("mail")) {
					AttachedMailQuestionType attached = target.addNewAttachedMailQuestion();
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				} else {
					LOGGER.warn("Type of open field is not known (text,nonumbers,number,grade,mail). Handeled as text");
					AttachedOpenQuestionType attached = target.addNewAttachedOpenQuestion();
					if (attachedOpenSource.isSetValidationMessage()) {
						String message = null;
						if (PageManager.getInstance().getMojo() != null) {
							message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(attachedOpenSource), attachedOpenSource.getValidationMessage());
						}
						attached.setValidationMessage(this.createElExpression(message));
					}
					addAttachedOpenQuestion(attachedOpenSource, attached);
				}
			}
		}
	}
	protected void addLabelFacetToAnswerOption(final AbstractLabeledAnswerOptionType source, final BaseOptionType target) throws XmlException {
		FacetType labels = null;
		if (source.getLabelArray().length > 0) {
			labels = target.addNewFacet();
			labels.setName(FacetType.Name.LABELS);
			for (final TextResponseOptionType label : source.getLabelArray()) {
				final String key = addTextToFacetAnswerOption(label, labels);
				if (key != null) {
					PageManager.getInstance().getMojo().writeLabelInformation(source.getUid(), getVariableName(), label.getVisible(), key);
				}
			}
		}
		if (source.getLabel2() != null) {
			if (labels == null) {
				labels = target.addNewFacet();
				labels.setName(FacetType.Name.LABELS);
			}
			final String key = addTextToFacetAnswerOption(this.createXmlTextTypeFromString(source, "label", source.getLabel2(), TextResponseOptionType.class), labels);
			if (key != null) {
				PageManager.getInstance().getMojo().writeLabelInformation(source.getUid(), getVariableName(), "true", key);
			}
		}
	}
}
