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
import com.sun.java.jsf.composite.answer.BaseOptionType;
import com.sun.java.jsf.composite.answer.MultipleOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.composite.MultipleChoiceCompositeItemType;
import com.sun.java.jsf.composite.composite.MultipleChoiceCompositeResponseDomainType;
import com.sun.java.jsf.composite.composite.MultipleChoiceCompositeResponseDomainUnitType;
import com.sun.java.jsf.composite.composite.MultipleChoiceCompositeType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixMultipleChoiceItemType;
import de.his.zofar.xml.questionnaire.MatrixMultipleChoiceResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixMultipleChoiceType;
import de.his.zofar.xml.questionnaire.MatrixMultipleChoiceUnitType;
import de.his.zofar.xml.questionnaire.MultipleChoiceItemType;
import de.his.zofar.xml.questionnaire.TextType;
/**
 * creator multiple choice matrices.
 *
 * @author le
 *
 */
class XhtmlMatrixMultipleChoiceCreator extends AbstractXhtmlMatrixCreator
        implements IXhtmlCreator {
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
            final SectionType targetSection,final boolean root) throws XmlException {
        createMatrix(source, targetSection.addNewMultipleChoiceComposite());
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
        createMatrix(source, target.addNewMultipleChoiceComposite());
    }
    private void createMatrix(final IdentificationalType source,
            final MultipleChoiceCompositeType target) throws XmlException {
        final MatrixMultipleChoiceType sourceMatrix = (MatrixMultipleChoiceType) source;
        setIdentifier(source, target);
        if (sourceMatrix.isSetVisible()) {
            target.setRendered(createElExpression(sourceMatrix
                    .getVisible()));
        }
        if (sourceMatrix.isSetHeader()) {
            addMatrixHeader(sourceMatrix.getHeader(), target);
        }
        addResponseDomain(sourceMatrix.getResponseDomain(),
                target.addNewMultipleChoiceCompositeResponseDomain());
    }
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addResponseDomain(
            final MatrixMultipleChoiceResponseDomainType source,
            final MultipleChoiceCompositeResponseDomainType target)
            throws XmlException {
        setMatrixResponseDomain(source, target);
        SortType sort = null;
        if (source.isSetSortCondition() || source.isSetSortMode()) {
            sort = createSortContainer(source, target.addNewSort());
        }
        for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
            if (MatrixMultipleChoiceItemType.class.isAssignableFrom(element
                    .getClass())) {
                MultipleChoiceCompositeItemType item = null;
                if (sort == null) {
                    item = target.addNewMultipleChoiceCompositeItem();
                } else {
                    item = sort.addNewMultipleChoiceCompositeItem();
                }
                addRowItem((MatrixMultipleChoiceItemType) element, item);
            } else if (MatrixMultipleChoiceUnitType.class
                    .isAssignableFrom(element.getClass())) {
                MultipleChoiceCompositeResponseDomainUnitType unit = null;
                if (sort == null) {
                    unit = target
                            .addNewMultipleChoiceCompositeResponseDomainUnit();
                } else {
                    unit = sort
                            .addNewMultipleChoiceCompositeResponseDomainUnit();
                }
                addUnitRecursively((MatrixMultipleChoiceUnitType) element,
                        unit);
            }
        }
    }
    private void addUnitRecursively(final MatrixMultipleChoiceUnitType source,
            final MultipleChoiceCompositeResponseDomainUnitType target)
            throws XmlException {
        if (source.isSetHeader()) {
            final FacetType header = target.addNewFacet();
            header.setName(FacetType.Name.HEADER);
            addTextToFacet(source.getHeader().getTitle(), header);
        }
        setIdentifier(source, target);
        if (source.isSetVisible()) {
            target.setRendered(createElExpression(source
                    .getVisible()));
        }
        SortType sort = null;
        if (source.isSetSortCondition() || source.isSetSortMode()) {
            sort = createSortContainer(source, target.addNewSort());
        }
        for (final XmlObject child : source.selectPath(SELECT_PATH_IN_ORDER)) {
            if (MatrixMultipleChoiceUnitType.class.isAssignableFrom(child
                    .getClass())) {
                MultipleChoiceCompositeResponseDomainUnitType unit = null;
                if (sort == null) {
                    unit = target
                            .addNewMultipleChoiceCompositeResponseDomainUnit();
                } else {
                    unit = sort
                            .addNewMultipleChoiceCompositeResponseDomainUnit();
                }
                addUnitRecursively((MatrixMultipleChoiceUnitType) child,
                        unit);
            } else if (MatrixMultipleChoiceItemType.class
                    .isAssignableFrom(child.getClass())) {
                MultipleChoiceCompositeItemType item = null;
                if (sort == null) {
                    item = target.addNewMultipleChoiceCompositeItem();
                } else {
                    item = sort.addNewMultipleChoiceCompositeItem();
                }
                addRowItem((MatrixMultipleChoiceItemType) child, item);
            }
        }
    }
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addRowItem(final MatrixMultipleChoiceItemType source,
            final MultipleChoiceCompositeItemType target) throws XmlException {
        setIdentifier(source, target);
        if (source.isSetVisible()) {
            target.setRendered(createElExpression(source
                    .getVisible()));
        }
        if (source.getHeader() != null) {
            final FacetType header = target.addNewFacet();
            header.setName(FacetType.Name.HEADER);
            for (final XmlObject text : source.getHeader().selectPath(
                    SELECT_PATH_IN_ORDER)) {
                if (TextType.class.isAssignableFrom(text.getClass())) {
                    addTextToFacet((TextType) text, header);
                }
            }
        }
		if(source.isSetAttachedOpen()){
			this.addMatrixAttachedOpen(source.getAttachedOpen(), target);
		}
        for (final MultipleChoiceItemType item : source.getResponseDomain()
                .getAnswerOptionArray()) {
            addItem(item, target.addNewMultipleOption());
        }
    }
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addItem(final MultipleChoiceItemType source,
            final MultipleOptionType target) throws XmlException {
        setIdentifier(source, target);
        target.setValue(createVariableReference(source.getVariable()+".value"));
        setVariableName(source.getVariable());
        if (source.isSetVisible()) {
            target.setRendered(createElExpression(source
                    .getVisible()));
        }
        addAbstractAnswerOption(source, target);
        if (source.isSetMissing()) {
            target.setMissing(source.getMissing());
        }
        if (source.isSetExclusive()) {
            target.setExclusive(source.getExclusive());
        }
    }
}
