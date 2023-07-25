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
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.answer.BooleanResponseDomainType;
import com.sun.java.jsf.composite.answer.DropDownMissingResponseDomainType;
import com.sun.java.jsf.composite.answer.LikertResponseDomainType;
import com.sun.java.jsf.composite.answer.RadioButtonSingleChoiceResponseDomainType;
import com.sun.java.jsf.composite.answer.RadioButtonSingleChoiceResponseDomainType.LabelPosition.Enum;
import com.sun.java.jsf.composite.answer.SelectBooleanOptionType;
import com.sun.java.jsf.composite.answer.SingleChoiceResponseDomainType;
import com.sun.java.jsf.composite.answer.SingleOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.question.BaseQuestionType;
import com.sun.java.jsf.composite.question.BooleanType;
import com.sun.java.jsf.composite.question.SingleChoiceType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.AttachedQuestionOpenType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceUnitType;
import de.his.zofar.xml.questionnaire.VariableType;
import de.his.zofar.xml.questionnaire.impl.QuestionSingleChoiceAnswerOptionTypeImpl;
/**
 * creator for single choice questions.
 * 
 * @author le
 * 
 */
class XhtmlQuestionSingleChoiceCreator extends AbstractXhtmlQuestionCreator
		implements IXhtmlCreator {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XhtmlQuestionSingleChoiceCreator.class);
	private static final String DROPDOWN_SUFFIX = "dropDown";
	private static final String MISSING_SUFFIX = "missing";
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#addToSection
	 * (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.container.SectionType)
	 */
	@Override
	public void addToSection(final IdentificationalType source,
			final SectionType targetSection, final boolean root)
			throws XmlException {
		BaseQuestionType xhtmlSingleChoice = null;
		final Boolean isBooleanQuestion = ((QuestionSingleChoiceType) source)
				.getResponseDomain().getType()
				.equals(QuestionSingleChoiceResponseDomainType.Type.BOOLEAN);
		if (isBooleanQuestion) {
			xhtmlSingleChoice = targetSection.addNewBoolean();
		} else {
			xhtmlSingleChoice = targetSection.addNewSingleChoice();
		}
		createQuestion(source, xhtmlSingleChoice);
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
	public void addToSort(final IdentificationalType source,
			final SortType target) throws XmlException {
		BaseQuestionType xhtmlSingleChoice = null;
		final Boolean isBooleanQuestion = ((QuestionSingleChoiceType) source)
				.getResponseDomain().getType()
				.equals(QuestionSingleChoiceResponseDomainType.Type.BOOLEAN);
		if (isBooleanQuestion) {
			xhtmlSingleChoice = target.addNewBoolean();
		} else {
			xhtmlSingleChoice = target.addNewSingleChoice();
		}
		createQuestion(source, xhtmlSingleChoice);
	}
	private void createQuestion(final IdentificationalType source,
			final BaseQuestionType target) throws XmlException {
		final QuestionSingleChoiceType sourceQuestion = (QuestionSingleChoiceType) source;
		final Boolean isBooleanQuestion = sourceQuestion.getResponseDomain()
				.getType()
				.equals(QuestionSingleChoiceResponseDomainType.Type.BOOLEAN);
		setIdentifier(source, target);
        if (sourceQuestion.isSetVisible()) {
            target.setRendered(createElExpression(sourceQuestion
                    .getVisible()));
        }
		addQuestionHeader(sourceQuestion.getHeader(), target);
		if (isBooleanQuestion) {
			addBooleanResponseDomain(sourceQuestion.getResponseDomain(),
					(BooleanType) target);
		} else {
			addResponseDomain(sourceQuestion.getResponseDomain(),
					(SingleChoiceType) target);
		}
	}
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @throws XmlException
	 */
	private void addResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceType targetSingleChoice) throws XmlException {
		final QuestionSingleChoiceResponseDomainType.Type.Enum type = source
				.getType();
		SingleChoiceResponseDomainType xhtmlResponseDomain;
		setVariableName(source.getVariable());
		switch (type.intValue()) {
		case QuestionSingleChoiceResponseDomainType.Type.INT_RADIO:
			xhtmlResponseDomain = createRadioResponseDomain(source,
					targetSingleChoice);
			break;
		case QuestionSingleChoiceResponseDomainType.Type.INT_DROPDOWN:
			xhtmlResponseDomain = createDropDownResponseDomain(source,
					targetSingleChoice);
			break;
		case QuestionSingleChoiceResponseDomainType.Type.INT_LIKERT:
			xhtmlResponseDomain = createLikertResponseDomain(source,
					targetSingleChoice);
			break;
		default:
			throw new IllegalStateException();
		}
		xhtmlResponseDomain.setValue(createVariableReference(source
				.getVariable() + ".valueId"));
		xhtmlResponseDomain
				.setVar(createVariableReference(source.getVariable()));
		setIdentifier(source, xhtmlResponseDomain);
		xhtmlResponseDomain.setShowValues(source.getShowValues());
		xhtmlResponseDomain.setMissingSeparated(source.getMissingSeparated());
		if ((source.isSetItemClasses()) && (source.getItemClasses())) {
			xhtmlResponseDomain.setItemClasses(ITEM_CLASSES);
		}
		if (!type.equals(QuestionSingleChoiceResponseDomainType.Type.DROPDOWN)
				|| !source.getMissingSeparated()) {
			addAnswerOptions(source, xhtmlResponseDomain,type);
		}
	}
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @return
	 * @throws XmlException
	 */
	private SingleChoiceResponseDomainType createDropDownResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceType targetSingleChoice) throws XmlException {
		SingleChoiceResponseDomainType xhtmlResponseDomain;
		boolean missingFlag = source.getMissingSeparated();
		if (missingFlag) {
			xhtmlResponseDomain = createDropDownMissingsResponseDomain(source,
					targetSingleChoice, missingFlag, true);
		} else {
			boolean hasAttached = false;
			final QuestionSingleChoiceAnswerOptionType[] answers = source
					.getAnswerOptionArray();
			for (final QuestionSingleChoiceAnswerOptionType answer : answers) {
				final AttachedQuestionOpenType[] attachedOpens = answer.getQuestionOpenArray();
				if ((attachedOpens != null)&&(attachedOpens.length > 0)) {
					hasAttached = true;
					break;
				}
			}
			if (hasAttached) {
				xhtmlResponseDomain = createDropDownMissingsResponseDomain(
						source, targetSingleChoice, missingFlag, hasAttached);
			} else {
				xhtmlResponseDomain = targetSingleChoice
						.addNewComboSingleChoiceResponseDomain();
			}
		}
		return xhtmlResponseDomain;
	}
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @return
	 * @throws XmlException
	 */
	private SingleChoiceResponseDomainType createDropDownMissingsResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceType targetSingleChoice,
			final boolean separateMissings, final boolean separateAttached)
			throws XmlException {
		final SingleChoiceResponseDomainType xhtmlResponseDomain = targetSingleChoice
				.addNewDropDownMissingResponseDomain();
		final FacetType dropdownFacet = ((DropDownMissingResponseDomainType) xhtmlResponseDomain)
				.addNewFacet();
		dropdownFacet.setName(FacetType.Name.DROP_DOWN);
		final SingleChoiceResponseDomainType dropdown = dropdownFacet
				.addNewComboSingleChoiceResponseDomain();
		addAnswerOptions(source, dropdown, true, false, !separateAttached,QuestionSingleChoiceResponseDomainType.Type.DROPDOWN);
		if (source.isSetItemClasses()) {
			dropdown.setItemClasses(ITEM_CLASSES);
		}
		final String dropdownVariableName = source.getVariable()
				+ DROPDOWN_SUFFIX;
		final VariableType dropdownVariable = VariableType.Factory
				.newInstance();
		dropdownVariable.setName(dropdownVariableName);
		dropdownVariable.setType(VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION);
		PageManager.getInstance().getAdditionalVariables()
				.add(dropdownVariable);
		final Map<String, String> dropdownOptions = retrieveAdditionalAnswerOptions(
				source, true, false);
		PageManager.getInstance().getAdditionalVariableOptions()
				.put(dropdownVariable, dropdownOptions);
		dropdown.setValue(createVariableReference(dropdownVariableName
				+ ".valueId"));
		dropdown.setId(DROPDOWN_SUFFIX);
		dropdown.setShowValues(source.getShowValues());
		final FacetType missingFacet = ((DropDownMissingResponseDomainType) xhtmlResponseDomain)
				.addNewFacet();
		missingFacet.setName(FacetType.Name.MISSING);
		final SingleChoiceResponseDomainType missing = missingFacet
				.addNewRadioButtonSingleChoiceResponseDomain();
		addAnswerOptions(source, missing, false, separateMissings,
				separateAttached,QuestionSingleChoiceResponseDomainType.Type.RADIO);
		if (source.isSetItemClasses()) {
			missing.setItemClasses(ITEM_CLASSES);
		}
		final String missingVariableName = source.getVariable()
				+ MISSING_SUFFIX;
		final VariableType missingVariable = VariableType.Factory.newInstance();
		missingVariable.setName(missingVariableName);
		missingVariable.setType(VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION);
		PageManager.getInstance().getAdditionalVariables().add(missingVariable);
		final Map<String, String> missingOptions = retrieveAdditionalAnswerOptions(
				source, false, separateMissings);
		PageManager.getInstance().getAdditionalVariableOptions()
				.put(missingVariable, missingOptions);
		missing.setValue(createVariableReference(missingVariableName
				+ ".valueId"));
		missing.setId(MISSING_SUFFIX);
		missing.setShowValues(source.getShowValues());
		return xhtmlResponseDomain;
	}
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @return
	 */
	private SingleChoiceResponseDomainType createLikertResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceType targetSingleChoice) {
		SingleChoiceResponseDomainType xhtmlResponseDomain;
		xhtmlResponseDomain = targetSingleChoice.addNewLikertResponseDomain();
		if (source.isSetShowMidScaleHeader()) {
			((LikertResponseDomainType) xhtmlResponseDomain)
					.setShowMidScaleHeader(source.getShowMidScaleHeader());
		}
		if (source.getDirection().equals(
				QuestionSingleChoiceResponseDomainType.Direction.HORIZONTAL)) {
			((LikertResponseDomainType) xhtmlResponseDomain)
					.setDirection("horizontal");
		}
		return xhtmlResponseDomain;
	}
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @return
	 */
	private SingleChoiceResponseDomainType createRadioResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceType targetSingleChoice) {
		SingleChoiceResponseDomainType xhtmlResponseDomain;
		switch (source.getDirection().intValue()) {
		case QuestionSingleChoiceResponseDomainType.Direction.INT_HORIZONTAL:
			xhtmlResponseDomain = targetSingleChoice
					.addNewRadioButtonSingleChoiceResponseDomain();
			((RadioButtonSingleChoiceResponseDomainType) xhtmlResponseDomain)
					.setDirection("horizontal");
			((RadioButtonSingleChoiceResponseDomainType) xhtmlResponseDomain)
					.setLabelPosition(getLabelPosition(source
							.getLabelPosition()));
			break;
		case QuestionSingleChoiceResponseDomainType.Direction.INT_VERTICAL:
			xhtmlResponseDomain = targetSingleChoice
					.addNewRadioButtonSingleChoiceResponseDomain();
			break;
		default:
			throw new IllegalStateException();
		}
		return xhtmlResponseDomain;
	}
	/**
	 * @param labelPosition
	 * @return
	 */
	private Enum getLabelPosition(
			final QuestionSingleChoiceResponseDomainType.LabelPosition.Enum labelPosition) {
		switch (labelPosition.intValue()) {
		case QuestionSingleChoiceResponseDomainType.LabelPosition.INT_BOTTOM:
			return RadioButtonSingleChoiceResponseDomainType.LabelPosition.BOTTOM;
		case QuestionSingleChoiceResponseDomainType.LabelPosition.INT_LEFT:
			return RadioButtonSingleChoiceResponseDomainType.LabelPosition.LEFT;
		case QuestionSingleChoiceResponseDomainType.LabelPosition.INT_RIGHT:
			return RadioButtonSingleChoiceResponseDomainType.LabelPosition.RIGHT;
		case QuestionSingleChoiceResponseDomainType.LabelPosition.INT_TOP:
			return RadioButtonSingleChoiceResponseDomainType.LabelPosition.TOP;
		default:
			throw new IllegalArgumentException("unknown label position "
					+ labelPosition);
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addAnswerOptions(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceResponseDomainType target,final QuestionSingleChoiceResponseDomainType.Type.Enum type) throws XmlException {
		addAnswerOptions(source, target, true, true, true,type);
	}
	/**
	 * @param source
	 * @param target
	 * @param addNonMissings
	 * @param addMissings
	 * @throws XmlException
	 */
	private Map<String, String> retrieveAdditionalAnswerOptions(
			final IdentificationalType source, final Boolean addNonMissings,
			final Boolean addMissings) throws XmlException {
		final Map<String, String> back = new LinkedHashMap<>();
		for (final XmlObject child : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (QuestionSingleChoiceUnitType.class.isAssignableFrom(child
					.getClass())) {
				back.putAll(retrieveAdditionalAnswerOptions(
						(QuestionSingleChoiceUnitType) child, addNonMissings,
						addMissings));
			} else if (QuestionSingleChoiceAnswerOptionType.class
					.isAssignableFrom(child.getClass())) {
				final Boolean isMissing = ((QuestionSingleChoiceAnswerOptionType) child)
						.getMissing();
				if ((addNonMissings && !isMissing)
						|| (addMissings && isMissing)) {
					final QuestionSingleChoiceAnswerOptionType tmp = (QuestionSingleChoiceAnswerOptionTypeImpl) child;
					back.put(tmp.getUid(), tmp.getValue());
				}
			}
		}
		return back;
	}
	/**
	 * @param source
	 * @param target
	 * @param addNonMissings
	 * @param addMissings
	 * @throws XmlException
	 */
	private void addAnswerOptions(
			final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceResponseDomainType target,
			final Boolean addNonMissings, final Boolean addMissings,
			final Boolean addAttached,final QuestionSingleChoiceResponseDomainType.Type.Enum type) throws XmlException {
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		for (final XmlObject child : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (QuestionSingleChoiceUnitType.class.isAssignableFrom(child
					.getClass())) {
				SectionType unit = null;
				if (sort == null) {
					unit = target.addNewSection();
				} else {
					unit = sort.addNewSection();
				}
				if (!addUnitRecursively((QuestionSingleChoiceUnitType) child,
						unit, addNonMissings, addMissings, addAttached,type)) {
					unit.getDomNode().getParentNode()
							.removeChild(unit.getDomNode());
				}
			} else if (QuestionSingleChoiceAnswerOptionType.class
					.isAssignableFrom(child.getClass())) {
				SingleOptionType singleOption = null;
				final Boolean isMissing = ((QuestionSingleChoiceAnswerOptionType) child)
						.getMissing();
				final AttachedQuestionOpenType[] attachedOpens = ((QuestionSingleChoiceAnswerOptionType) child).getQuestionOpenArray();
				final Boolean isAttached = ((attachedOpens != null)&&(attachedOpens.length > 0));
				if ((addNonMissings && !isMissing && !isAttached)
						|| (addMissings && isMissing)
						|| (addAttached && isAttached)) {
					if (sort == null) {
						singleOption = target.addNewSingleOption();
					} else {
						singleOption = sort.addNewSingleOption();
					}
					addAnswerOption(
							(QuestionSingleChoiceAnswerOptionType) child,
							singleOption);
				}
			}
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private boolean addUnitRecursively(
			final QuestionSingleChoiceUnitType source,
			final SectionType target, final Boolean addNonMissings,
			final Boolean addMissings, final Boolean addAttached,final QuestionSingleChoiceResponseDomainType.Type.Enum type)
			throws XmlException {
		boolean back = false;
		if (source.isSetHeader()) {
			final FacetType header = target.addNewFacet();
			header.setName(FacetType.Name.HEADER);
			addTextToFacet(source.getHeader().getTitle(), header);
		}
		setIdentifier(source, target);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		for (final XmlObject child : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (QuestionSingleChoiceUnitType.class.isAssignableFrom(child
					.getClass())) {
				SectionType unit = null;
				if (sort == null) {
					unit = target.addNewSection();
				} else {
					unit = sort.addNewSection();
				}
				if (!addUnitRecursively((QuestionSingleChoiceUnitType) child,
						unit, addNonMissings, addMissings, addAttached,type)) {
					unit.getDomNode().getParentNode()
							.removeChild(unit.getDomNode());
				} else {
					back = true;
				}
			} else if (QuestionSingleChoiceAnswerOptionType.class
					.isAssignableFrom(child.getClass())) {
				SingleOptionType singleOption = null;
				final Boolean isMissing = ((QuestionSingleChoiceAnswerOptionType) child)
						.getMissing();
				final AttachedQuestionOpenType[] attachedOpens = ((QuestionSingleChoiceAnswerOptionType) child).getQuestionOpenArray();
				final Boolean isAttached = ((attachedOpens != null)&&(attachedOpens.length > 0));
				if ((addNonMissings && !isMissing && !isAttached)
						|| (addMissings && isMissing)
						|| (!addNonMissings && addAttached && isAttached)) {
					if (sort == null) {
						singleOption = target.addNewSingleOption();
					} else {
						singleOption = sort.addNewSingleOption();
					}
					addAnswerOption(
							(QuestionSingleChoiceAnswerOptionType) child,
							singleOption);
					back = true;
				}
				else{
					if(type != QuestionSingleChoiceResponseDomainType.Type.DROPDOWN){
						if ((addNonMissings && addAttached && isAttached)) {
							if (sort == null) {
								singleOption = target.addNewSingleOption();
							} else {
								singleOption = sort.addNewSingleOption();
							}
							addAnswerOption((QuestionSingleChoiceAnswerOptionType) child, singleOption);
							back = true;
						}
					}
				}
			}
		}
		return back;
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addAnswerOption(
			final QuestionSingleChoiceAnswerOptionType source,
			final SingleOptionType target) throws XmlException {
		target.setItemValue(source.getValue());
		addAbstractAnswerOption(source, target);
	}
	/**
	 * @param source
	 * @param targetBoolean
	 * @throws XmlException
	 */
	private void addBooleanResponseDomain(
			final QuestionSingleChoiceResponseDomainType source,
			final BooleanType targetBoolean) throws XmlException {
		final String variableReference = createVariableReference(source
				.getVariable());
		final BooleanResponseDomainType xhtmlResponseDomain = targetBoolean
				.addNewBooleanResponseDomain();
		setIdentifier(source, xhtmlResponseDomain);
		for (final QuestionSingleChoiceAnswerOptionType answerOption : source
				.getAnswerOptionArray()) {
			final SelectBooleanOptionType xhtmlBooleanOption = xhtmlResponseDomain
					.addNewSelectBooleanOption();
			setIdentifier(answerOption, xhtmlBooleanOption);
			xhtmlBooleanOption.setVar(variableReference);
		}
	}
}
