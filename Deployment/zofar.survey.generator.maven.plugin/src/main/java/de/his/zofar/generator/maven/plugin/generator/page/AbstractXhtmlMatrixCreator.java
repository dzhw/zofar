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

import com.sun.java.jsf.composite.answer.AttachedGradeQuestionType;
import com.sun.java.jsf.composite.answer.AttachedNoNumbersQuestionType;
import com.sun.java.jsf.composite.answer.AttachedNumberQuestionType;
import com.sun.java.jsf.composite.answer.AttachedOpenQuestionType;
import com.sun.java.jsf.composite.answer.SingleOptionType;
import com.sun.java.jsf.composite.composite.MultipleChoiceCompositeItemType;
import com.sun.java.jsf.composite.matrix.BaseMatrixResponseDomainType;
import com.sun.java.jsf.composite.matrix.BaseMatrixType;
import com.sun.java.jsf.composite.matrix.OpenMatrixItemType;
import com.sun.java.jsf.composite.matrix.SingleChoiceMatrixItemType;
import com.sun.java.jsf.core.FacetType;

import de.his.zofar.xml.questionnaire.AbstractMatrixResponseDomainType;
import de.his.zofar.xml.questionnaire.AttachedQuestionOpenType;
import de.his.zofar.xml.questionnaire.MatrixHeaderType;
import de.his.zofar.xml.questionnaire.TextType;

/**
 * bundles all methods that common for all matrices.
 *
 * @author le
 *
 */
abstract class AbstractXhtmlMatrixCreator extends AbstractXhtmlQuestionCreator {

    /**
     * adds the matrix header to the matrices.
     *
     * @param source
     * @param targetMatrix
     * @throws XmlException
     */
    protected void addMatrixHeader(final MatrixHeaderType source,
            final BaseMatrixType targetMatrix) throws XmlException {
        final FacetType headerFacet = targetMatrix.addNewFacet();
        headerFacet.setName(FacetType.Name.HEADER);

        final XmlObject[] children = source.selectPath(SELECT_PATH_IN_ORDER);
        for (final XmlObject child : children) {
            if (de.his.zofar.xml.questionnaire.TextType.class
                    .isAssignableFrom(child.getClass())) {
                addTextToQuestionHeader(
                        (de.his.zofar.xml.questionnaire.TextType) child,
                        headerFacet);
            }
        }
    }

    /**
     * sets all mandatory attributes and elements of a matrix response domain.
     *
     * @param source
     * @param target
     * @throws XmlException
     */
    protected void setMatrixResponseDomain(
            final AbstractMatrixResponseDomainType source,
            final BaseMatrixResponseDomainType target) throws XmlException {
        setIdentifier(source, target);

        target.setNoResponseOptions(source.getNoResponseOptions());

        if (source.isSetScaleHeader()) {
            final FacetType scaleHeader = target.addNewFacet();
            scaleHeader.setName(FacetType.Name.SCALE_HEADER);
            addTextToFacet(source.getScaleHeader().getTitle(), scaleHeader);
        }

        if (source.isSetHeader()) {
            final FacetType header = target.addNewFacet();
            header.setName(FacetType.Name.HEADER);
            for (final TextType title : source.getHeader().getTitleArray()) {
                addTextToFacet(title, header);
            }
        }

        if (source.isSetMissingHeader()) {
            final FacetType missingHeader = target.addNewFacet();
            missingHeader.setName(FacetType.Name.MISSING_HEADER);
            for (final TextType title : source.getMissingHeader()
                    .getTitleArray()) {
                addTextToFacet(title, missingHeader);
            }
        }

        if (source.isSetQuestionColumnEnabled()) {
            target.setQuestionColumn(source.getQuestionColumnEnabled());
        }
    }
    
    protected void addMatrixAttachedOpen(final AttachedQuestionOpenType source, final Object target ) throws XmlException{
    	final String type = source.getType();
    	
    	if(target == null)return;
    	
		if (type.equals("text")) {
			AttachedOpenQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedOpenQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedOpenQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedOpenQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedOpenQuestion();
			else return;
						
			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);
		} 
		else if (type.equals("nonumbers")) {
			AttachedNoNumbersQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedNoNumbersQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedNoNumbersQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedNoNumbersQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedNoNumbersQuestion();
			else return;

			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);
		}	
		else if (type.equals("number")) {
			AttachedNumberQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedNumberQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedNumberQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedNumberQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedNumberQuestion();
			else return;
			
			if ((source).isSetMinValue())
				attached.setMinValue(source.getMinValue());
			if (source.isSetMaxValue())
				attached.setMaxValue(source.getMaxValue());
			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);
		} else if (type.equals("grade")) {
			AttachedGradeQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedGradeQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedGradeQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedGradeQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedGradeQuestion();
			else return;
			
			if ((source).isSetMinValue())
				attached.setMinValue(source.getMinValue());
			if (source.isSetMaxValue())
				attached.setMaxValue(source.getMaxValue());
			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);

		} else if (type.equals("mail")) {
			AttachedOpenQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedMailQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedMailQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedMailQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedMailQuestion();
			else return;

			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);
		} else {
			LOGGER.warn("Type of open field is not known (text,nonumbers,number,grade,mail). Handeled as text");
			AttachedOpenQuestionType attached = null;
			if((OpenMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((OpenMatrixItemType)target).addNewAttachedOpenQuestion();
			else if((MultipleChoiceCompositeItemType.class).isAssignableFrom(target.getClass()))attached = ((MultipleChoiceCompositeItemType)target).addNewAttachedOpenQuestion();
			else if((SingleChoiceMatrixItemType.class).isAssignableFrom(target.getClass()))attached = ((SingleChoiceMatrixItemType)target).addNewAttachedOpenQuestion();
			else if((SingleOptionType.class).isAssignableFrom(target.getClass()))attached = ((SingleOptionType)target).addNewAttachedOpenQuestion();
			else return;

			if (source.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(source), source.getValidationMessage());
				}
				attached.setValidationMessage(this.createElExpression(message));
			}
			addAttachedOpenQuestion(source, attached);
		}
    }
}
