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
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.composite.DoubleMatrixItemType;
import com.sun.java.jsf.composite.composite.DoubleMatrixResponseDomainType;
import com.sun.java.jsf.composite.composite.DoubleMatrixType;
import com.sun.java.jsf.composite.composite.DoubleMatrixUnitType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixItemResponseDomainType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixDoubleItemType;
import de.his.zofar.xml.questionnaire.MatrixDoubleResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixDoubleType;
import de.his.zofar.xml.questionnaire.MatrixQuestionDoubleUnitType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceAnswerOptionType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
import de.his.zofar.xml.questionnaire.TextType;
/**
 * creator for double matrices.
 *
 * @author le
 *
 */
class XhtmlMatrixDoubleSingleChoiceCreator extends AbstractXhtmlMatrixCreator implements IXhtmlCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(XhtmlMatrixDoubleSingleChoiceCreator.class);
	/*
	 * (non-Javadoc)
	 *
	 * @see de.his.zofar.generator.maven.plugin.generator.page.IXhtmlCreator#
	 * addToSection (de.his.zofar.xml.questionnaire.IdentificationalType,
	 * com.sun.java.jsf.composite.container.SectionType)
	 */
	@Override
	public void addToSection(final IdentificationalType source, final SectionType targetSection, final boolean root) throws XmlException {
		createElement(source, targetSection.addNewDoubleMatrix());
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
		createElement(source, target.addNewDoubleMatrix());
	}
	private void createElement(final IdentificationalType source, final DoubleMatrixType target) throws XmlException {
		final MatrixDoubleType sourceMatrix = (MatrixDoubleType) source;
		setIdentifier(source, target);
		if (sourceMatrix.isSetVisible()) {
			target.setRendered(createElExpression(sourceMatrix.getVisible()));
		}
		if (sourceMatrix.isSetHeader()) {
			addMatrixHeader(sourceMatrix.getHeader(), target);
		}
		addResponseDomain(sourceMatrix.getResponseDomain(), target.addNewDoubleMatrixResponseDomain());
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addResponseDomain(final MatrixDoubleResponseDomainType source, final DoubleMatrixResponseDomainType target) throws XmlException {
		setIdentifier(source, target);
		target.setNoResponseOptions(source.getNoResponseOptions());
		if (source.isSetIsDifferential()) {
			target.setIsDifferential(source.getIsDifferential());
		}
		if (source.isSetIsShowValues()) {
			target.setIsShowValues(source.getIsShowValues());
		}
		if (source.getShowPipes()) {
			target.setItemClasses(PIPE_CLASSES);
		}
		final FacetType leftScaleHeader = target.addNewFacet();
		leftScaleHeader.setName(FacetType.Name.LEFT_SCALE_HEADER);
		addTextToFacet(source.getLeftScaleHeader().getTitle(), leftScaleHeader);
		final FacetType rightScaleHeader = target.addNewFacet();
		rightScaleHeader.setName(FacetType.Name.RIGHT_SCALE_HEADER);
		addTextToFacet(source.getRightScaleHeader().getTitle(), rightScaleHeader);
		final List<String> leftLabels = new ArrayList<String>();
		if (source.isSetLeftHeader()) {
			for (final TextType title : source.getLeftHeader().getTitleArray()) {
				leftLabels.add(title.newCursor().getTextValue());
			}
		}
		if (source.isSetLeftMissingHeader()) {
			for (final TextType title : source.getLeftMissingHeader().getTitleArray()) {
				leftLabels.add(title.newCursor().getTextValue());
			}
		}
		final List<String> rightLabels = new ArrayList<String>();
		if (source.isSetRightHeader()) {
			for (final TextType title : source.getRightHeader().getTitleArray()) {
				rightLabels.add(title.newCursor().getTextValue());
			}
		}
		if (source.isSetRightMissingHeader()) {
			for (final TextType title : source.getRightMissingHeader().getTitleArray()) {
				rightLabels.add(title.newCursor().getTextValue());
			}
		}
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (MatrixDoubleItemType.class.isAssignableFrom(element.getClass())) {
				addRowItem((MatrixDoubleItemType) element, target.addNewDoubleMatrixItem(), leftLabels, rightLabels);
			} else if (MatrixQuestionDoubleUnitType.class.isAssignableFrom(element.getClass())) {
				addUnitRecursively((MatrixQuestionDoubleUnitType) element, target.addNewDoubleMatrixUnit(), leftLabels, rightLabels);
			}
		}
	}
	private void addUnitRecursively(final MatrixQuestionDoubleUnitType source, final DoubleMatrixUnitType target, final List<String> leftLabels, final List<String> rightLabels) throws XmlException {
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
			if (MatrixQuestionDoubleUnitType.class.isAssignableFrom(child.getClass())) {
				DoubleMatrixUnitType unit = null;
				if (sort == null) {
					unit = target.addNewDoubleMatrixUnit();
				} else {
					unit = sort.addNewDoubleMatrixUnit();
				}
				addUnitRecursively((MatrixQuestionDoubleUnitType) child, unit, leftLabels, rightLabels);
			} else if (MatrixDoubleItemType.class.isAssignableFrom(child.getClass())) {
				DoubleMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewDoubleMatrixItem();
				} else {
					item = sort.addNewDoubleMatrixItem();
				}
				addRowItem((MatrixDoubleItemType) child, item, leftLabels, rightLabels);
			} else {
				LOGGER.info("not implemented yet {}", child.getClass());
			}
		}
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addRowItem(final MatrixDoubleItemType source, final DoubleMatrixItemType target, final List<String> leftLabels, final List<String> rightLabels) throws XmlException {
		setIdentifier(source, target);
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		final FacetType questionFacet = target.addNewFacet();
		questionFacet.setName(FacetType.Name.QUESTION);
		for (final XmlObject element : source.getQuestion().selectPath(SELECT_PATH_IN_ORDER)) {
			if (TextType.class.isAssignableFrom(element.getClass())) {
				addTextToFacet((TextType) element, questionFacet);
			}
		}
		final FacetType left = target.addNewFacet();
		left.setName(FacetType.Name.LEFT);
		addSingleChoiceItem(source.getLeft(), left.addNewSingleChoiceMatrixItemResponseDomain(), leftLabels);
		final FacetType right = target.addNewFacet();
		right.setName(FacetType.Name.RIGHT);
		addSingleChoiceItem(source.getRight(), right.addNewSingleChoiceMatrixItemResponseDomain(), rightLabels);
	}
	/**
	 * @param source
	 * @param target
	 * @throws XmlException
	 */
	private void addSingleChoiceItem(final QuestionSingleChoiceResponseDomainType source, final SingleChoiceMatrixItemResponseDomainType target, final List<String> labels) throws XmlException {
		setIdentifier(source, target);
		target.setValue(createVariableReference(source.getVariable() + ".valueId"));
		int lft = 0;
		for (final QuestionSingleChoiceAnswerOptionType option : source.getAnswerOptionArray()) {
			final XhtmlMatrixSingleChoiceCreator creator = new XhtmlMatrixSingleChoiceCreator();
			String label = null;
			if (labels.size() >= lft)
				label = labels.get(lft);
			if (label != null) {
				final TextResponseOptionType newText = option.addNewLabel();
				newText.setUid("label");
				newText.newCursor().setTextValue(label);
			}
			creator.addRowOption(option, target.addNewSingleOption());
			lft = lft + 1;
		}
	}
}
