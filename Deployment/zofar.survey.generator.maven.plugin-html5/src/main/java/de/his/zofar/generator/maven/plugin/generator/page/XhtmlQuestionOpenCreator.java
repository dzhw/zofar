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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.answer.LargeOpenOptionType;
import com.sun.java.jsf.composite.answer.OpenGradeOptionType;
import com.sun.java.jsf.composite.answer.OpenMailOptionType;
import com.sun.java.jsf.composite.answer.OpenMonthpickerOptionType;
import com.sun.java.jsf.composite.answer.OpenNoNumbersOptionType;
import com.sun.java.jsf.composite.answer.OpenNumberOptionType;
import com.sun.java.jsf.composite.answer.OpenResponseDomainType;
import com.sun.java.jsf.composite.answer.SmallOpenOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.question.OpenType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
/**
 * creator for open questions.
 * 
 * @author le
 * 
 */
class XhtmlQuestionOpenCreator extends AbstractXhtmlQuestionCreator implements IXhtmlCreator {
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private static final String DEFAULT_RESPONSE_DOMAIN_UID = "responsedomain";
	private static final String DEFAULT_RESPONSE_OPTION_UID = "response";
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#addToSection
	 * (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.container.SectionType)
	 */
	@Override
	public void addToSection(final IdentificationalType source, final SectionType targetSection, final boolean root) throws XmlException {
		createElement(source, targetSection.addNewOpen());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#addToSort
	 * (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.common.SortType)
	 */
	@Override
	public void addToSort(final IdentificationalType source, final SortType target) throws XmlException {
		createElement(source, target.addNewOpen());
	}
	private void createElement(final IdentificationalType source, final OpenType target) throws XmlException {
		final QuestionOpenType sourceQuestion = (QuestionOpenType) source;
		setIdentifier(source, target);
		if (sourceQuestion.isSetVisible()) {
			target.setRendered(createElExpression(sourceQuestion.getVisible()));
		}
		addQuestionHeader(sourceQuestion.getHeader(), target);
		final String variableReference = createVariableReference(sourceQuestion.getVariable());
		final OpenResponseDomainType responseDomain = target.addNewOpenResponseDomain();
		responseDomain.setId(DEFAULT_RESPONSE_DOMAIN_UID);
		if (sourceQuestion.getType().equals("text")) {
			if (sourceQuestion.getSmallOption()) {
				SmallOpenOptionType openOption = responseDomain.addNewSmallOpenOption();
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				if (sourceQuestion.isSetSize()) {
					openOption.setSize(sourceQuestion.getSize().intValue());
				}
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
			} else {
				LargeOpenOptionType openOption = responseDomain.addNewLargeOpenOption();
				if (sourceQuestion.isSetRows()) {
					openOption.setRows(sourceQuestion.getRows().intValue());
				}
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				if (sourceQuestion.isSetColumns()) {
					openOption.setColumns(sourceQuestion.getColumns().intValue());
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
			}
		} else if (sourceQuestion.getType().equals("nonumbers")) {
			if (sourceQuestion.getSmallOption()) {
				OpenNoNumbersOptionType openOption = responseDomain.addNewOpenNoNumbersOption();
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				if (sourceQuestion.isSetSize()) {
					openOption.setSize(sourceQuestion.getSize().intValue());
				}
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
			} else {
				LargeOpenOptionType openOption = responseDomain.addNewLargeOpenNoNumbersOption();
				if (sourceQuestion.isSetRows()) {
					openOption.setRows(sourceQuestion.getRows().intValue());
				}
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				if (sourceQuestion.isSetColumns()) {
					openOption.setColumns(sourceQuestion.getColumns().intValue());
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
			}
		}
		else if (sourceQuestion.getType().equals("number")) {
			OpenNumberOptionType openOption = responseDomain.addNewOpenNumberOption();
			if (sourceQuestion.isSetMinValue())
				openOption.setMinValue(sourceQuestion.getMinValue());
			if (sourceQuestion.isSetMaxValue())
				openOption.setMaxValue(sourceQuestion.getMaxValue());
			if (sourceQuestion.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
				}
				((OpenNumberOptionType) openOption).setValidationMessage(this.createElExpression(message));
			}
			openOption.setVar(variableReference);
			openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetPrefix()) {
				final FacetType prefix = openOption.addNewFacet();
				prefix.setName(FacetType.Name.PREFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
					addTextToFacet(text, prefix);
				}
			}
			if (sourceQuestion.isSetPostfix()) {
				final FacetType postfix = openOption.addNewFacet();
				postfix.setName(FacetType.Name.POSTFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
					addTextToFacet(text, postfix);
				}
			}
			if (sourceQuestion.isSetInfield()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
				}
				if(message != null)openOption.setInfieldText(createElExpression(message));
			}
			if (sourceQuestion.isSetList()) {
				openOption.setList(sourceQuestion.getList());
			}
		} else if (sourceQuestion.getType().equals("grade")) {
			OpenGradeOptionType openOption = responseDomain.addNewOpenGradeOption();
			if (sourceQuestion.isSetMinValue())
				openOption.setMinValue(sourceQuestion.getMinValue());
			if (sourceQuestion.isSetMaxValue())
				openOption.setMaxValue(sourceQuestion.getMaxValue());
			if (sourceQuestion.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
				}
				openOption.setValidationMessage(this.createElExpression(message));
			}
			openOption.setVar(variableReference);
			openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetPrefix()) {
				final FacetType prefix = openOption.addNewFacet();
				prefix.setName(FacetType.Name.PREFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
					addTextToFacet(text, prefix);
				}
			}
			if (sourceQuestion.isSetPostfix()) {
				final FacetType postfix = openOption.addNewFacet();
				postfix.setName(FacetType.Name.POSTFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
					addTextToFacet(text, postfix);
				}
			}
			if (sourceQuestion.isSetInfield()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
				}
				if(message != null)openOption.setInfieldText(createElExpression(message));
			}
		} 
		else if (sourceQuestion.getType().equals("monthpicker")) {
			OpenMonthpickerOptionType openOption = responseDomain.addNewOpenMonthpickerOption();
			if (sourceQuestion.isSetMinRange())
				openOption.setMinRange(sourceQuestion.getMinRange());
			if (sourceQuestion.isSetMaxRange())
				openOption.setMaxRange(sourceQuestion.getMaxRange());
			if (sourceQuestion.isSetViewDate())
				openOption.setViewDate(sourceQuestion.getViewDate());
			if (sourceQuestion.isSetDatesDisabled())
				openOption.setDatesDisabled(sourceQuestion.getDatesDisabled());
			openOption.setVar(variableReference);
			openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
			if (sourceQuestion.isSetPrefix()) {
				final FacetType prefix = openOption.addNewFacet();
				prefix.setName(FacetType.Name.PREFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
					addTextToFacet(text, prefix);
				}
			}
			if (sourceQuestion.isSetPostfix()) {
				final FacetType postfix = openOption.addNewFacet();
				postfix.setName(FacetType.Name.POSTFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
					addTextToFacet(text, postfix);
				}
			}
			if (sourceQuestion.isSetInfield()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
				}
				if(message != null)openOption.setInfieldText(createElExpression(message));
			}
		}
		else if (sourceQuestion.getType().equals("mail")) {
			OpenMailOptionType openOption = responseDomain.addNewOpenMailOption();
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
				}
				openOption.setValidationMessage(this.createElExpression(message));
			}
			openOption.setVar(variableReference);
			openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
			if (sourceQuestion.isSetPrefix()) {
				final FacetType prefix = openOption.addNewFacet();
				prefix.setName(FacetType.Name.PREFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
					addTextToFacet(text, prefix);
				}
			}
			if (sourceQuestion.isSetPostfix()) {
				final FacetType postfix = openOption.addNewFacet();
				postfix.setName(FacetType.Name.POSTFIX);
				for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
					addTextToFacet(text, postfix);
				}
			}
			if (sourceQuestion.isSetInfield()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
				}
				if(message != null)openOption.setInfieldText(createElExpression(message));
			}
		} else {
			LOGGER.warn("Type of open field is not known (text,number,grade,mail). Handeled as text");
			if (sourceQuestion.getSmallOption()) {
				SmallOpenOptionType openOption = responseDomain.addNewSmallOpenOption();
				if (sourceQuestion.isSetSize()) {
					openOption.setSize(sourceQuestion.getSize().intValue());
				}
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
			} else {
				LargeOpenOptionType openOption = responseDomain.addNewLargeOpenOption();
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				if (sourceQuestion.isSetRows()) {
					openOption.setRows(sourceQuestion.getRows().intValue());
				}
				if (sourceQuestion.isSetColumns()) {
					openOption.setColumns(sourceQuestion.getColumns().intValue());
				}
				openOption.setVar(variableReference);
				openOption.setId(DEFAULT_RESPONSE_OPTION_UID);
				if (sourceQuestion.isSetPrefix()) {
					final FacetType prefix = openOption.addNewFacet();
					prefix.setName(FacetType.Name.PREFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPrefix().getLabelArray()) {
						addTextToFacet(text, prefix);
					}
				}
				if (sourceQuestion.isSetPostfix()) {
					final FacetType postfix = openOption.addNewFacet();
					postfix.setName(FacetType.Name.POSTFIX);
					for (final TextResponseOptionType text : sourceQuestion.getPostfix().getLabelArray()) {
						addTextToFacet(text, postfix);
					}
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
			}
		}
	}
}
