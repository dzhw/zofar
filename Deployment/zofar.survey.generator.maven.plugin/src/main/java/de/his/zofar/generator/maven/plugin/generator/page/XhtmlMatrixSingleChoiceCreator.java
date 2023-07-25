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
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.answer.ComboSingleChoiceResponseDomainType;
import com.sun.java.jsf.composite.answer.DropDownMissingResponseDomainType;
import com.sun.java.jsf.composite.answer.SingleChoiceResponseDomainType;
import com.sun.java.jsf.composite.answer.SingleOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixItemResponseDomainType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixItemType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixResponseDomainType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixResponseDomainUnitType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.AttachedQuestionOpenType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceItemType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.MatrixQuestionSingleChoiceUnitType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceUnitType;
import de.his.zofar.xml.questionnaire.TextType;
import de.his.zofar.xml.questionnaire.VariableType;
/**
 * creator for single choice matrices.
 * 
 * @author le
 * 
 */
class XhtmlMatrixSingleChoiceCreator extends AbstractXhtmlMatrixCreator implements IXhtmlCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(XhtmlMatrixSingleChoiceCreator.class);
	private static final String DROPDOWN_SUFFIX = "dropDown";
	private static final String MISSING_SUFFIX = "missing";
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#
	 * addToSection (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.container.SectionType)
	 */
	@Override
	public void addToSection(final IdentificationalType source, final SectionType targetSection, final boolean root)
			throws XmlException {
		createMatrix(source, targetSection.addNewSingleChoiceMatrix());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#
	 * addToSort (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.common.SortType)
	 */
	@Override
	public void addToSort(final IdentificationalType source, final SortType target) throws XmlException {
		createMatrix(source, target.addNewSingleChoiceMatrix());
	}
	public void createMatrix(final IdentificationalType source, final SingleChoiceMatrixType target)
			throws XmlException {
		final MatrixQuestionSingleChoiceType sourceMatrix = (MatrixQuestionSingleChoiceType) source;
		setIdentifier(source, target);
		if (sourceMatrix.isSetVisible()) {
			target.setRendered(createElExpression(sourceMatrix.getVisible()));
		}
		if (sourceMatrix.isSetHeader()) {
			addMatrixHeader(sourceMatrix.getHeader(), target);
		}
		addResponseDomain(sourceMatrix.getResponseDomain(), target.addNewSingleChoiceMatrixResponseDomain());
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addResponseDomain(final MatrixQuestionSingleChoiceResponseDomainType source,
			final SingleChoiceMatrixResponseDomainType target) throws XmlException {
		setMatrixResponseDomain(source, target);
		if (source.isSetIsDifferential()) {
			target.setIsDifferential(source.getIsDifferential());
		}
		if (source.isSetIsShowValues()) {
			target.setIsShowValues(source.getIsShowValues());
		}
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		if (source.isSetItemClasses()) {
			target.setItemClasses(ITEM_CLASSES);
		}
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (MatrixQuestionSingleChoiceItemType.class.isAssignableFrom(element.getClass())) {
				SingleChoiceMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewSingleChoiceMatrixItem();
				} else {
					item = sort.addNewSingleChoiceMatrixItem();
				}
				addRowItem((MatrixQuestionSingleChoiceItemType) element, item);
			} else if (MatrixQuestionSingleChoiceUnitType.class.isAssignableFrom(element.getClass())) {
				SingleChoiceMatrixResponseDomainUnitType unit = null;
				if (sort == null) {
					unit = target.addNewSingleChoiceMatrixResponseDomainUnit();
				} else {
					unit = sort.addNewSingleChoiceMatrixResponseDomainUnit();
				}
				addUnitRecursively((MatrixQuestionSingleChoiceUnitType) element, unit);
			}
		}
	}
	private void addUnitRecursively(final MatrixQuestionSingleChoiceUnitType source,
			final SingleChoiceMatrixResponseDomainUnitType target) throws XmlException {
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
			if (MatrixQuestionSingleChoiceUnitType.class.isAssignableFrom(child.getClass())) {
				SingleChoiceMatrixResponseDomainUnitType unit = null;
				if (sort == null) {
					unit = target.addNewSingleChoiceMatrixResponseDomainUnit();
				} else {
					unit = sort.addNewSingleChoiceMatrixResponseDomainUnit();
				}
				addUnitRecursively((MatrixQuestionSingleChoiceUnitType) child, unit);
			} else if (MatrixQuestionSingleChoiceItemType.class.isAssignableFrom(child.getClass())) {
				SingleChoiceMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewSingleChoiceMatrixItem();
				} else {
					item = sort.addNewSingleChoiceMatrixItem();
				}
				addRowItem((MatrixQuestionSingleChoiceItemType) child, item);
			}
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addRowItem(final MatrixQuestionSingleChoiceItemType source, final SingleChoiceMatrixItemType target)
			throws XmlException {
		setIdentifier(source, target);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		if (source.getHeader() != null) {
			final FacetType header = target.addNewFacet();
			header.setName(FacetType.Name.HEADER);
			for (final XmlObject text : source.getHeader().selectPath(SELECT_PATH_IN_ORDER)) {
				if (TextType.class.isAssignableFrom(text.getClass())) {
					addTextToFacet((TextType) text, header);
				}
			}
		}
		if (source.isSetAttachedOpen()) {
			this.addMatrixAttachedOpen(source.getAttachedOpen(), target);
		}
		if (source.getResponseDomain().getType().equals(QuestionSingleChoiceResponseDomainType.Type.RADIO)) {
			this.addRowItemResponseDomain(source.getResponseDomain(),
					target.addNewSingleChoiceMatrixItemResponseDomain());
		} else if (source.getResponseDomain().getType().equals(QuestionSingleChoiceResponseDomainType.Type.DROPDOWN)) {
			if (source.getResponseDomain().getMissingSeparated()) {
				this.addRowItemResponseDomain(source.getResponseDomain(), target.addNewDropDownMissingResponseDomain());
			} else
				this.addRowItemResponseDomain(source.getResponseDomain(),
						target.addNewComboSingleChoiceResponseDomain());
		}
	}
	/*************************************************************************************/
	private boolean addUnitRecursivelyCombo(final QuestionSingleChoiceUnitType source, SectionType target,
			final SingleChoiceResponseDomainType dropdown) throws XmlException {
		boolean back = false;
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		if (sort == null) {
			target.addNewSection();
		}
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		final Map<String, String> dropdownOptions = new HashMap<>();
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (QuestionSingleChoiceUnitType.class.isAssignableFrom(element.getClass())) {
				QuestionSingleChoiceUnitType optionUnit = null;
				optionUnit = (QuestionSingleChoiceUnitType) element;
				if (sort == null) {
					target = dropdown.addNewSection();
				} else {
				}
				if (optionUnit.isSetVisible()) {
					target.setRendered(createElExpression(optionUnit.getVisible()));
				}
				setIdentifier(optionUnit, dropdown);
				if (!addUnitRecursivelyCombo(optionUnit, target, dropdown)) {
				} else {
					back = true;
				}
			} else if (QuestionSingleChoiceAnswerOptionType.class.isAssignableFrom(element.getClass())) {
				QuestionSingleChoiceAnswerOptionType option = null;
				option = (QuestionSingleChoiceAnswerOptionType) element;
				if (sort == null) {
					addRowOption((QuestionSingleChoiceAnswerOptionType) element, target.addNewSingleOption(), true);
				} else {
					addRowOption((QuestionSingleChoiceAnswerOptionType) element, sort.addNewSingleOption(), true);
				}
				dropdownOptions.put(option.getUid(), option.getValue());
			}
		}
		return back;
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addRowItemResponseDomain(final QuestionSingleChoiceResponseDomainType source,
			final DropDownMissingResponseDomainType target) throws XmlException {
		target.setValue(createVariableReference(source.getVariable() + ".valueId"));
		target.setVar("#{" + source.getVariable() + "}");
		this.setVariableName(source.getVariable());
		final FacetType dropdownFacet = target.addNewFacet();
		dropdownFacet.setName(FacetType.Name.DROP_DOWN);
		final SingleChoiceResponseDomainType dropdown = dropdownFacet.addNewComboSingleChoiceResponseDomain();
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, dropdown.addNewSort());
		}
		if (source.isSetItemClasses()) {
			target.setItemClasses(ITEM_CLASSES);
		}
		final Map<String, String> dropdownOptions = new HashMap<>();
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (QuestionSingleChoiceUnitType.class.isAssignableFrom(element.getClass())) {
				QuestionSingleChoiceUnitType optionUnit = null;
				optionUnit = (QuestionSingleChoiceUnitType) element;
				SectionType unitSection = null;
				if (sort == null) {
					unitSection = dropdown.addNewSection();
				} else {
					unitSection = sort.addNewSection();
				}
				if (optionUnit.isSetVisible()) {
					unitSection.setRendered(createElExpression(optionUnit.getVisible()));
				}
				if (optionUnit.isSetHeader()) {
					FacetType header = unitSection.addNewFacet();
					header.setName(FacetType.Name.HEADER);
					addTitleToFacet(optionUnit.getHeader().getTitle(), header);
				}
				if (!addUnitRecursivelyCombo(optionUnit, unitSection, dropdown)) {
				}
			} else if (QuestionSingleChoiceAnswerOptionType.class.isAssignableFrom(element.getClass())) {
				QuestionSingleChoiceAnswerOptionType option = null;
				option = (QuestionSingleChoiceAnswerOptionType) element;
				if (!option.getMissing()) {
					if (sort == null) {
						addRowOption((QuestionSingleChoiceAnswerOptionType) element, dropdown.addNewSingleOption(),
								true);
					} else {
						addRowOption((QuestionSingleChoiceAnswerOptionType) element, sort.addNewSingleOption(), true);
					}
					dropdownOptions.put(option.getUid(), option.getValue());
				}
			}
		}
		if (source.isSetItemClasses()) {
			dropdown.setItemClasses(ITEM_CLASSES);
		}
		final String dropdownVariableName = source.getVariable() + DROPDOWN_SUFFIX;
		final VariableType dropdownVariable = VariableType.Factory.newInstance();
		dropdownVariable.setName(dropdownVariableName);
		dropdownVariable.setType(VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION);
		PageManager.getInstance().getAdditionalVariables().add(dropdownVariable);
		PageManager.getInstance().getAdditionalVariableOptions().put(dropdownVariable, dropdownOptions);
		dropdown.setValue(createVariableReference(dropdownVariableName + ".valueId"));
		dropdown.setId(DROPDOWN_SUFFIX);
		dropdown.setShowValues(source.getShowValues());
		final FacetType missingFacet = target.addNewFacet();
		missingFacet.setName(FacetType.Name.MISSING);
		final SingleChoiceMatrixItemResponseDomainType missing = missingFacet
				.addNewSingleChoiceMatrixItemResponseDomain();
		final Map<String, String> missingOptions = new HashMap<>();
		for (final QuestionSingleChoiceAnswerOptionType option : source.getAnswerOptionArray()) {
			if (option.getMissing()) {
				addRowOption(option, missing.addNewSingleOption());
				missingOptions.put(option.getUid(), option.getValue());
			}
		}
		final String missingVariableName = source.getVariable() + MISSING_SUFFIX;
		final VariableType missingVariable = VariableType.Factory.newInstance();
		missingVariable.setName(missingVariableName);
		missingVariable.setType(VariableType.Type.SINGLE_CHOICE_ANSWER_OPTION);
		PageManager.getInstance().getAdditionalVariables().add(missingVariable);
		PageManager.getInstance().getAdditionalVariableOptions().put(missingVariable, missingOptions);
		missing.setValue(createVariableReference(missingVariableName + ".valueId"));
		missing.setId(MISSING_SUFFIX);
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addRowItemResponseDomain(final QuestionSingleChoiceResponseDomainType source,
			final ComboSingleChoiceResponseDomainType target) throws XmlException {
		setIdentifier(source, target);
		target.setValue(createVariableReference(source.getVariable() + ".valueId"));
		setVariableName(source.getVariable());
		for (final QuestionSingleChoiceAnswerOptionType option : source.getAnswerOptionArray()) {
			addRowOption(option, target.addNewSingleOption());
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addRowItemResponseDomain(final QuestionSingleChoiceResponseDomainType source,
			final SingleChoiceMatrixItemResponseDomainType target) throws XmlException {
		setIdentifier(source, target);
		target.setValue(createVariableReference(source.getVariable() + ".valueId"));
		setVariableName(source.getVariable());
		for (final QuestionSingleChoiceAnswerOptionType option : source.getAnswerOptionArray()) {
			addRowOption(option, target.addNewSingleOption());
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	void addRowOption(final QuestionSingleChoiceAnswerOptionType source, final SingleOptionType target)
			throws XmlException {
		addRowOption(source, target, false);
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	void addRowOption(final QuestionSingleChoiceAnswerOptionType source, final SingleOptionType target,
			final boolean addLabel) throws XmlException {
		setIdentifier(source, target);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		addLabelFacetToAnswerOption(source, target);
		target.setItemValue(source.getValue());
		if (source.isSetMissing()) {
			target.setMissing(source.getMissing());
		}
		final AttachedQuestionOpenType[] attachedOpensSource = source.getQuestionOpenArray();
		if ((attachedOpensSource != null) && (attachedOpensSource.length > 0)) {
			final int count = attachedOpensSource.length;
			for (int a = 0; a < count; a++) {
				final AttachedQuestionOpenType attachedOpenSource = attachedOpensSource[a];
				this.addMatrixAttachedOpen(attachedOpenSource, target);
			}
		}
	}
}
