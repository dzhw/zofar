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
import com.sun.java.jsf.composite.answer.MultipleChoiceResponseDomainType;
import com.sun.java.jsf.composite.answer.MultipleOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.question.MultipleChoiceType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MultipleChoiceItemType;
import de.his.zofar.xml.questionnaire.MultipleChoiceUnitType;
/**
 * creator for multiple choices.
 *
 * @author le
 *
 */
class XhtmlMultipleChoiceCreator extends AbstractXhtmlQuestionCreator implements
        IXhtmlCreator {
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
        createMultipleChoice(source, targetSection.addNewMultipleChoice());
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
        createMultipleChoice(source, target.addNewMultipleChoice());
    }
    private void createMultipleChoice(final IdentificationalType source,
            final MultipleChoiceType target) throws XmlException {
        final de.his.zofar.xml.questionnaire.MultipleChoiceType sourceMc = (de.his.zofar.xml.questionnaire.MultipleChoiceType) source;
        setIdentifier(source, target);
        if (sourceMc.isSetVisible()) {
            target.setRendered(createElExpression(sourceMc
                    .getVisible()));
        }
        addQuestionHeader(sourceMc.getHeader(), target);
        addResponseDomain(sourceMc.getResponseDomain(),createResponseDomain(sourceMc.getResponseDomain(),target));
    }
	/**
	 * @param source
	 * @param targetSingleChoice
	 * @return
	 */
	private MultipleChoiceResponseDomainType createResponseDomain(
			final de.his.zofar.xml.questionnaire.MultipleChoiceResponseDomainType source,
			final MultipleChoiceType targetMultipleChoice) {
		MultipleChoiceResponseDomainType xhtmlResponseDomain = targetMultipleChoice.addNewMultipleChoiceResponseDomain();
		switch (source.getDirection().intValue()) {
		case de.his.zofar.xml.questionnaire.MultipleChoiceResponseDomainType.Direction.INT_HORIZONTAL:
			((MultipleChoiceResponseDomainType) xhtmlResponseDomain).setDirection("horizontal");
			break;
		case de.his.zofar.xml.questionnaire.MultipleChoiceResponseDomainType.Direction.INT_VERTICAL:
			break;
		default:
			throw new IllegalStateException();
		}
		return xhtmlResponseDomain;
	}
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addResponseDomain(
            final de.his.zofar.xml.questionnaire.MultipleChoiceResponseDomainType source,
            final MultipleChoiceResponseDomainType target) throws XmlException {
        setIdentifier(source, target);
        target.setMissingSeparated(source.getMissingSeparated());
        SortType sort = null;
        if (source.isSetSortCondition() || source.isSetSortMode()) {
            sort = createSortContainer(source, target.addNewSort());
        }
        if (source.isSetItemClasses()) {
        	target.setItemClasses(ITEM_CLASSES);
        }
        for (final XmlObject child : source.selectPath(SELECT_PATH_IN_ORDER)) {
            if (MultipleChoiceItemType.class.isAssignableFrom(child.getClass())) {
                MultipleOptionType option = null;
                if (sort == null) {
                    option = target.addNewMultipleOption();
                } else {
                    option = sort.addNewMultipleOption();
                }
                addMultipleChoiceItem((MultipleChoiceItemType) child,
                        option);
            } else if (MultipleChoiceUnitType.class.isAssignableFrom(child
                    .getClass())) {
                SectionType unit = null;
                if (sort == null) {
                    unit = target.addNewSection();
                } else {
                    unit = sort.addNewSection();
                }
                addUnitRecursively((MultipleChoiceUnitType) child, unit);
            }
        }
    }
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addUnitRecursively(final MultipleChoiceUnitType source,
            final SectionType target) throws XmlException {
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
            if (MultipleChoiceUnitType.class.isAssignableFrom(child.getClass())) {
                SectionType unit = null;
                if (sort == null) {
                    unit = target.addNewSection();
                } else {
                    unit = sort.addNewSection();
                }
                addUnitRecursively((MultipleChoiceUnitType) child, unit);
            } else if (MultipleChoiceItemType.class.isAssignableFrom(child
                    .getClass())) {
                MultipleOptionType option = null;
                if (sort == null) {
                    option = target.addNewMultipleOption();
                } else {
                    option = sort.addNewMultipleOption();
                }
                addMultipleChoiceItem((MultipleChoiceItemType) child,
                        option);
            }
        }
    }
    /**
     * @param source
     * @param target
     * @throws XmlException
     */
    private void addMultipleChoiceItem(final MultipleChoiceItemType source,
            final MultipleOptionType target) throws XmlException {
        target.setValue(createVariableReference(source.getVariable()+ ".value"));
        setVariableName(source.getVariable());
        addAbstractAnswerOption(source, target);
        if (source.isSetMissing()) {
            target.setMissing(source.getMissing());
        }
        if (source.isSetExclusive()) {
            target.setExclusive(source.getExclusive());
        }
    }
}
