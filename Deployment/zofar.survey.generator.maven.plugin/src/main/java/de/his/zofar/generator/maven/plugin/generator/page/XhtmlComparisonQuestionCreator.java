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
import com.sun.java.jsf.composite.composite.ComparisonQuestionItemType;
import com.sun.java.jsf.composite.composite.ComparisonQuestionResponseDomainType;
import com.sun.java.jsf.composite.composite.ComparisonQuestionType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.ComparisonItemType;
import de.his.zofar.xml.questionnaire.ComparisonResponseDomainType;
import de.his.zofar.xml.questionnaire.ComparisonType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.SortableContainerType;
import de.his.zofar.xml.questionnaire.impl.ComparisonItemTypeImpl;
import de.his.zofar.xml.questionnaire.impl.TextTypeImpl;
public class XhtmlComparisonQuestionCreator extends AbstractXhtmlMatrixCreator
		implements IXhtmlCreator {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(XhtmlComparisonQuestionCreator.class);
	public XhtmlComparisonQuestionCreator() {
		super();
	}
	@Override
	public void addToSection(IdentificationalType source, SectionType target,
			boolean root) throws XmlException {
		createQuestion(source, target.addNewComparisonQuestion());
	}
	@Override
	public void addToSort(IdentificationalType source, SortType target)
			throws XmlException {
		createQuestion(source, target.addNewComparisonQuestion());
	}
	private void createQuestion(final IdentificationalType source,
			final ComparisonQuestionType target) throws XmlException {
		final ComparisonType sourceQuestion = (ComparisonType) source;
		setIdentifier(source, target);
		if (sourceQuestion.isSetVisible()) {
			target.setRendered(createElExpression(sourceQuestion.getVisible()));
		}
		addMatrixHeader(sourceQuestion.getHeader(), target);
		addResponseDomain(sourceQuestion.getResponseDomain(),
				target.addNewComparisonResponseDomain());
	}
	private void addHeaderRecursively(final XmlObject source,
	final org.apache.xmlbeans.XmlObject target) throws XmlException {
		if(source == null)return;
		if(target == null)return;
		if (de.his.zofar.xml.questionnaire.impl.TextTypeImpl.class
                .isAssignableFrom(source.getClass())) {
			TextTypeImpl tmp = (TextTypeImpl)source;
        	if((FacetType.class).isAssignableFrom(target.getClass())){
        		final FacetType targetTmp = (FacetType)target;
        		addTitleToFacet(tmp,targetTmp);
        	}
        	else if((com.sun.java.jsf.composite.composite.ComparisonUnitType.class).isAssignableFrom(target.getClass())){
        		final com.sun.java.jsf.composite.composite.ComparisonUnitType targetTmp = (com.sun.java.jsf.composite.composite.ComparisonUnitType)target;
        		addTextToComparsionUnit(tmp,targetTmp);
        	}     
        	else if((com.sun.java.jsf.composite.common.SortType.class).isAssignableFrom(target.getClass())){
        		final com.sun.java.jsf.composite.common.SortType targetTmp = (com.sun.java.jsf.composite.common.SortType)target;
        		addTextToSort(tmp,targetTmp);
        	}
        }
        else if((de.his.zofar.xml.questionnaire.impl.ComparisonUnitTypeImpl.class).isAssignableFrom(source.getClass())){
        	de.his.zofar.xml.questionnaire.impl.ComparisonUnitTypeImpl tmp = (de.his.zofar.xml.questionnaire.impl.ComparisonUnitTypeImpl)source;
        	final String uid = tmp.getUid();
        	String visible = null;
        	if(tmp.isSetVisible())visible = createElExpression(tmp.getVisible());
        	com.sun.java.jsf.composite.composite.ComparisonUnitType unit = null;
        	if((FacetType.class).isAssignableFrom(target.getClass())){
        		final FacetType targetTmp = (FacetType)target;
        		unit = targetTmp.addNewComparisonUnit();
        	}
        	else if((com.sun.java.jsf.composite.composite.ComparisonUnitType.class).isAssignableFrom(target.getClass())){
        		final com.sun.java.jsf.composite.composite.ComparisonUnitType targetTmp = (com.sun.java.jsf.composite.composite.ComparisonUnitType)target;
        		unit = targetTmp.addNewComparisonUnit();
        	}
        	else if((com.sun.java.jsf.composite.common.SortType.class).isAssignableFrom(target.getClass())){
        		final com.sun.java.jsf.composite.common.SortType targetTmp = (com.sun.java.jsf.composite.common.SortType)target;
        		unit = targetTmp.addNewComparisonUnit();
        	}
        	if(unit != null){
        		unit.setId(uid);
        		if(visible != null)unit.setRendered(visible);
                if (tmp.isSetSortCondition() || tmp.isSetSortMode()) {
                    final SortType sort = createSortContainer(tmp,
                    		unit.addNewSort());
                    final XmlObject[] headerItems = tmp.getHeader().selectPath(SELECT_PATH_IN_ORDER);
        			final FacetType headerFacet = unit.addNewFacet();
        			headerFacet.setName(FacetType.Name.HEADER);
        	        for (final XmlObject item : headerItems) {
        	        	addHeaderRecursively(item,headerFacet);
        	        }
        	        final XmlObject[] items = tmp.selectPath(SELECT_PATH_IN_ORDER);
        	        for (final XmlObject item : items) {
        	        	addHeaderRecursively(item,sort);
        	        }
                }
                else{
        	        final XmlObject[] items = tmp.selectPath(SELECT_PATH_IN_ORDER);
        	        for (final XmlObject item : items) {
        	        	addHeaderRecursively(item,unit);
        	        }
                }
        	}
        }
        else if((de.his.zofar.xml.questionnaire.impl.UnitHeaderTypeImpl.class).isAssignableFrom(source.getClass())){
        	de.his.zofar.xml.questionnaire.impl.UnitHeaderTypeImpl tmp = (de.his.zofar.xml.questionnaire.impl.UnitHeaderTypeImpl)source;
        	FacetType header = null;
        	if((FacetType.class).isAssignableFrom(target.getClass())){
        		final FacetType targetTmp = (FacetType)target;
        		header = targetTmp;
        	}
        	else if((com.sun.java.jsf.composite.composite.ComparisonUnitType.class).isAssignableFrom(target.getClass())){
        		final com.sun.java.jsf.composite.composite.ComparisonUnitType targetTmp = (com.sun.java.jsf.composite.composite.ComparisonUnitType)target;
        		header = targetTmp.addNewFacet();
        		header.setName(FacetType.Name.HEADER);
        	}
        	final XmlObject[] headerItems = tmp.selectPath(SELECT_PATH_IN_ORDER);
	        for (final XmlObject headerItem : headerItems) {
	        	addHeaderRecursively(headerItem,header);
	        }
        }
        else{
        	LOGGER.info("unkown class : {} {}",source.getDomNode().getNodeName(),source.getClass());
        }
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
	protected SortType createSortContainer(final SortableContainerType source,
			final SortType target) throws XmlException {
		if (!source.isSetSortCondition() && !source.isSetSortMode()) {
			return null;
		}
		target.setId("sort");
		target.setSorted(createElExpression(source.getSortCondition()));
		target.setBean("#{sortBean}");
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
				throw new RuntimeException("sort mode " + source.getSortMode()
						+ " is either not yet implemened or not supported");
			}
			target.setMode(mode);
		}
		return target;
	}
	private void addResponseDomain(
			final ComparisonResponseDomainType source,
			final ComparisonQuestionResponseDomainType target) throws XmlException {
		if(source == null)return;
		setIdentifier(source, target);
		if(source.getHeader() != null){
			final FacetType headerFacet = target.addNewFacet();
			headerFacet.setName(FacetType.Name.HEADER);
	        final XmlObject[] headerItems = source.getHeader().selectPath(SELECT_PATH_IN_ORDER);
	        for (final XmlObject headerItem : headerItems) {
	        	addHeaderRecursively(headerItem,headerFacet);
	        }
		}
		if(source.getMissingHeader() != null){
			final FacetType missingFacet = target.addNewFacet();
			missingFacet.setName(FacetType.Name.MISSING_HEADER);
	        final XmlObject[] missingItems = source.getMissingHeader().selectPath(SELECT_PATH_IN_ORDER);
	        for (final XmlObject missingItem : missingItems) {
	        	addHeaderRecursively(missingItem,missingFacet);
	        }
		}
		final ComparisonItemType[] items = source.getItemArray();
        for (final ComparisonItemType item : items) {
        	addItem(item,target.addNewComparisonItem());
        }
	}
	private void addItem(final XmlObject source,
			final ComparisonQuestionItemType target) throws XmlException {
		if((ComparisonItemTypeImpl.class).isAssignableFrom(source.getClass())){
			setIdentifier((ComparisonItemTypeImpl)source, target);
			for (final XmlObject object : source.selectPath(SELECT_PATH_IN_ORDER)) {
				if(!(IdentificationalType.class).isAssignableFrom(object.getClass()))continue;
				final IXhtmlCreator creator = CreatorFactory.newCreator((IdentificationalType)object);
				if(creator != null){
					creator.addToSection((IdentificationalType)object, target, false);
				}
			}
		}
	}
}
