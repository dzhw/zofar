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
package de.his.zofar.generator.maven.plugin.generator.page;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.composite.MixedMatrixItemType;
import com.sun.java.jsf.composite.composite.MixedMatrixResponseDomainType;
import com.sun.java.jsf.composite.composite.MixedMatrixType;
import com.sun.java.jsf.composite.composite.MixedMatrixUnitType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.question.BaseQuestionType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.xml.questionnaire.AbstractQuestionType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixQuestionMixedItemType;
import de.his.zofar.xml.questionnaire.MatrixQuestionMixedResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixQuestionMixedType;
import de.his.zofar.xml.questionnaire.MatrixQuestionMixedUnitType;
import de.his.zofar.xml.questionnaire.QuestionSingleChoiceType;
import de.his.zofar.xml.questionnaire.SectionBodyType;
import de.his.zofar.xml.questionnaire.TextType;
public class XhtmlMatrixQuestionMixedCreator extends AbstractXhtmlMatrixCreator
		implements IXhtmlCreator {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XhtmlMatrixQuestionMixedCreator.class);
	public XhtmlMatrixQuestionMixedCreator() {
		super();
	}
	@Override
	public void addToSection(IdentificationalType source, SectionType target,
			boolean root) throws XmlException {
		createMatrix(source, target.addNewMixedMatrix());
	}
	@Override
	public void addToSort(IdentificationalType source, SortType target)
			throws XmlException {
		createMatrix(source, target.addNewMixedMatrix());
	}
	private void createMatrix(final IdentificationalType source,
			final MixedMatrixType target) throws XmlException {
		final MatrixQuestionMixedType sourceMatrix = (MatrixQuestionMixedType) source;
		setIdentifier(source, target);
		addMatrixHeader(sourceMatrix.getHeader(), target);
		if (sourceMatrix.isSetVisible()) {
			target.setRendered(createElExpression(sourceMatrix.getVisible()));
		}
		addResponseDomain(sourceMatrix.getResponseDomain(),
				target.addNewMixedMatrixResponseDomain());
	}
	private void addResponseDomain(
			final MatrixQuestionMixedResponseDomainType source,
			final MixedMatrixResponseDomainType target) throws XmlException {
		setMatrixResponseDomain(source, target);
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		if (source.isSetItemClasses()) {
			target.setItemClasses(ITEM_CLASSES);
		}
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (MatrixQuestionMixedItemType.class.isAssignableFrom(element
					.getClass())) {
				MixedMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewMixedMatrixItem();
				} else {
					item = sort.addNewMixedMatrixItem();
				}
				addRow((MatrixQuestionMixedItemType) element, item);
			} else if (MatrixQuestionMixedUnitType.class
					.isAssignableFrom(element.getClass())) {
				MixedMatrixUnitType unit = null;
				if (sort == null) {
					unit = target.addNewMixedMatrixUnit();
				} else {
					unit = sort.addNewMixedMatrixUnit();
				}
				addUnitRecursively((MatrixQuestionMixedUnitType) element, unit);
			}
		}
	}
	private void addUnitRecursively(final MatrixQuestionMixedUnitType source,
			final MixedMatrixUnitType target) throws XmlException {
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
			if (MatrixQuestionMixedUnitType.class.isAssignableFrom(child
					.getClass())) {
				MixedMatrixUnitType unit = null;
				if (sort == null) {
					unit = target.addNewMixedMatrixUnit();
				} else {
					unit = sort.addNewMixedMatrixUnit();
				}
				addUnitRecursively((MatrixQuestionMixedUnitType) child, unit);
			} else if (MatrixQuestionMixedItemType.class.isAssignableFrom(child
					.getClass())) {
				MixedMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewMixedMatrixItem();
				} else {
					item = sort.addNewMixedMatrixItem();
				}
				addRow((MatrixQuestionMixedItemType) child, item);
			}
		}
	}
	private void addRow(final MatrixQuestionMixedItemType source,
			final MixedMatrixItemType target) throws XmlException {
		setIdentifier(source, target);
		if (source.getHeader() != null) {
			final FacetType header = target.addNewFacet();
			header.setName(FacetType.Name.HEADER);
			for (final XmlObject object : source.getHeader().selectPath(
					SELECT_PATH_IN_ORDER)) {
				if (TextType.class.isAssignableFrom(object.getClass())) {
					addTextToFacet((TextType) object, header);
				}
			}
		}
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		for (final XmlObject object : source.getBody().selectPath(
				SELECT_PATH_IN_ORDER)) {
			addItem(object, target);
		}
	}
	private void addItem(final XmlObject source,
			final MixedMatrixItemType target) throws XmlException {
		if((IdentificationalType.class).isAssignableFrom(source.getClass())){
			final IdentificationalType tmp = (IdentificationalType)source;
			final IXhtmlCreator creator = CreatorFactory.newCreator(tmp);
			if(creator != null){
				if((AbstractQuestionType.class).isAssignableFrom(tmp.getClass())){
					final AbstractQuestionType question = (AbstractQuestionType)tmp;
					if(question.getHeader() == null)question.addNewHeader();
				}
				creator.addToSection(tmp, target, false);
			}
		}
	}
}
