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
import com.sun.java.jsf.composite.answer.LargeOpenOptionType;
import com.sun.java.jsf.composite.answer.OpenGradeOptionType;
import com.sun.java.jsf.composite.answer.OpenMailOptionType;
import com.sun.java.jsf.composite.answer.OpenNumberOptionType;
import com.sun.java.jsf.composite.answer.SmallOpenOptionType;
import com.sun.java.jsf.composite.common.SortType;
import com.sun.java.jsf.composite.container.SectionType;
import com.sun.java.jsf.composite.matrix.OpenMatrixItemType;
import com.sun.java.jsf.composite.matrix.OpenMatrixResponseDomainType;
import com.sun.java.jsf.composite.matrix.OpenMatrixResponseDomainUnitType;
import com.sun.java.jsf.composite.matrix.OpenMatrixType;
import com.sun.java.jsf.core.FacetType;
import de.his.zofar.xml.questionnaire.IdentificationalType;
import de.his.zofar.xml.questionnaire.MatrixQuestionOpenItemType;
import de.his.zofar.xml.questionnaire.MatrixQuestionOpenResponseDomainType;
import de.his.zofar.xml.questionnaire.MatrixQuestionOpenType;
import de.his.zofar.xml.questionnaire.MatrixQuestionOpenUnitType;
import de.his.zofar.xml.questionnaire.QuestionOpenPrefixType;
import de.his.zofar.xml.questionnaire.QuestionOpenType;
import de.his.zofar.xml.questionnaire.TextResponseOptionType;
import de.his.zofar.xml.questionnaire.TextType;
/**
 * creator for open question matrices.
 * 
 * @author le
 * 
 */
class XhtmlMatrixOpenCreator extends AbstractXhtmlMatrixCreator implements IXhtmlCreator {
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
		createMatrix(source, targetSection.addNewOpenMatrix());
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
		createMatrix(source, target.addNewOpenMatrix());
	}
	private void createMatrix(final IdentificationalType source, final OpenMatrixType target) throws XmlException {
		final MatrixQuestionOpenType sourceMatrix = (MatrixQuestionOpenType) source;
		setIdentifier(source, target);
		addMatrixHeader(sourceMatrix.getHeader(), target);
		if (sourceMatrix.isSetVisible()) {
			target.setRendered(createElExpression(sourceMatrix.getVisible()));
		}
		addResponseDomain(sourceMatrix.getResponseDomain(), target.addNewOpenMatrixResponseDomain());
	}
	private void addResponseDomain(final MatrixQuestionOpenResponseDomainType source, final OpenMatrixResponseDomainType target) throws XmlException {
		setMatrixResponseDomain(source, target);
		if (source.isSetHeader()) {
			final TextType[] titles = source.getHeader().getTitleArray();
			for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
				if (MatrixQuestionOpenItemType.class.isAssignableFrom(element.getClass())) {
					final MatrixQuestionOpenItemType item = (MatrixQuestionOpenItemType)element;
					int index = 0;
					for (final QuestionOpenType question : item.getQuestionArray()) {
						QuestionOpenPrefixType prefix = question.addNewPrefix();
						TextResponseOptionType prefixLabel = prefix.addNewLabel();
						((IdentificationalType)prefixLabel).setUid(question.getUid()+"_prefix"+index);
						prefixLabel.newCursor().setTextValue(titles[index].newCursor().getTextValue());
						index = index + 1;
					}
				}
			}
		}
		SortType sort = null;
		if (source.isSetSortCondition() || source.isSetSortMode()) {
			sort = createSortContainer(source, target.addNewSort());
		}
		for (final XmlObject element : source.selectPath(SELECT_PATH_IN_ORDER)) {
			if (MatrixQuestionOpenItemType.class.isAssignableFrom(element.getClass())) {
				OpenMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewOpenMatrixItem();
				} else {
					item = sort.addNewOpenMatrixItem();
				}
				if (source.isSetHeader()) {
					final FacetType itemHeader = item.addNewFacet();
					itemHeader.setName(FacetType.Name.COLUMNS);
					for (final XmlObject object : source.getHeader().selectPath(SELECT_PATH_IN_ORDER)) {
						if (TextType.class.isAssignableFrom(object.getClass())) {
							addTextToFacet((TextType) object, itemHeader);
						}
					}
				}
				addRow((MatrixQuestionOpenItemType) element, item);
			} else if (MatrixQuestionOpenUnitType.class.isAssignableFrom(element.getClass())) {
				OpenMatrixResponseDomainUnitType unit = null;
				if (sort == null) {
					unit = target.addNewOpenMatrixResponseDomainUnit();
				} else {
					unit = sort.addNewOpenMatrixResponseDomainUnit();
				}
				addUnitRecursively((MatrixQuestionOpenUnitType) element, unit);
			}
		}
	}
	private void addUnitRecursively(final MatrixQuestionOpenUnitType source, final OpenMatrixResponseDomainUnitType target) throws XmlException {
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
			if (MatrixQuestionOpenUnitType.class.isAssignableFrom(child.getClass())) {
				OpenMatrixResponseDomainUnitType unit = null;
				if (sort == null) {
					unit = target.addNewOpenMatrixResponseDomainUnit();
				} else {
					unit = sort.addNewOpenMatrixResponseDomainUnit();
				}
				addUnitRecursively((MatrixQuestionOpenUnitType) child, unit);
			} else if (MatrixQuestionOpenItemType.class.isAssignableFrom(child.getClass())) {
				OpenMatrixItemType item = null;
				if (sort == null) {
					item = target.addNewOpenMatrixItem();
				} else {
					item = sort.addNewOpenMatrixItem();
				}
				addRow((MatrixQuestionOpenItemType) child, item);
			}
		}
	}
	private void addRow(final MatrixQuestionOpenItemType source, final OpenMatrixItemType target) throws XmlException {
		setIdentifier(source, target);
		if (source.getHeader() != null) {
			final FacetType header = target.addNewFacet();
			header.setName(FacetType.Name.HEADER);
			for (final XmlObject object : source.getHeader().selectPath(SELECT_PATH_IN_ORDER)) {
				if (TextType.class.isAssignableFrom(object.getClass())) {
					addTextToFacet((TextType) object, header);
				}
			}
		}
		if (source.isSetVisible()) {
			target.setRendered(createElExpression(source.getVisible()));
		}
		if (source.isSetAttachedOpen()) {
			this.addMatrixAttachedOpen(source.getAttachedOpen(), target);
		}
		for (final QuestionOpenType question : source.getQuestionArray()) {
			setIdentifier(source, target);
			addItem(question, target);
		}
	}
	private void addItem(final QuestionOpenType source, final OpenMatrixItemType target) throws XmlException {
		createElement(source, target);
	}
	private void createElement(final IdentificationalType source, final OpenMatrixItemType target) throws XmlException {
		final QuestionOpenType sourceQuestion = (QuestionOpenType) source;
		if (sourceQuestion.isSetVisible()) {
			target.setRendered(createElExpression(sourceQuestion.getVisible()));
		}
		final String variableReference = createVariableReference(sourceQuestion.getVariable());
		if (sourceQuestion.getType().equals("text")) {
			if (sourceQuestion.getSmallOption()) {
				SmallOpenOptionType openOption = target.addNewSmallOpenOption();
				setIdentifier(source, openOption);
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
				openOption.setVar(variableReference);
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
				if (sourceQuestion.isSetList()) {
					openOption.setList(sourceQuestion.getList());
				}
				if (sourceQuestion.isSetInfield()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion)+".placeholder", sourceQuestion.getInfield());
					}
					if(message != null)openOption.setInfieldText(createElExpression(message));
				}
			} else {
				LargeOpenOptionType openOption = target.addNewLargeOpenOption();
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
		} else if (sourceQuestion.getType().equals("number")) {
			OpenNumberOptionType openOption = target.addNewOpenNumberOption();
			setIdentifier(source, openOption);
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
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
			OpenGradeOptionType openOption = target.addNewOpenGradeOption();
			setIdentifier(source, openOption);
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
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
		} else if (sourceQuestion.getType().equals("mail")) {
			OpenMailOptionType openOption = target.addNewOpenMailOption();
			setIdentifier(source, openOption);
			if (sourceQuestion.isSetMaxLength()) {
				openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
			}
			if (sourceQuestion.isSetSize()) {
				openOption.setSize(sourceQuestion.getSize().intValue());
			}
			if (sourceQuestion.isSetValidationMessage()) {
				String message = null;
				if (PageManager.getInstance().getMojo() != null) {
					message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
				}
				openOption.setValidationMessage(this.createElExpression(message));
			}
			openOption.setVar(variableReference);
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
			LOGGER.warn("Type of open field is not known (text,number,grade,mail). Handeled as text");
			if (sourceQuestion.getSmallOption()) {
				SmallOpenOptionType openOption = target.addNewSmallOpenOption();
				setIdentifier(source, openOption);
				if (sourceQuestion.isSetMaxLength()) {
					openOption.setMaxlength(sourceQuestion.getMaxLength().intValue());
				}
				if (sourceQuestion.isSetSize()) {
					openOption.setSize(sourceQuestion.getSize().intValue());
				}
				if (sourceQuestion.isSetValidationMessage()) {
					String message = null;
					if (PageManager.getInstance().getMojo() != null) {
						message = PageManager.getInstance().getMojo().addTextToBundle(generateUid(sourceQuestion), sourceQuestion.getValidationMessage());
					}
					openOption.setValidationMessage(this.createElExpression(message));
				}
				openOption.setVar(variableReference);
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
				LargeOpenOptionType openOption = target.addNewLargeOpenOption();
				setIdentifier(source, openOption);
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
		if (sourceQuestion.isSetVisible()) {
			target.setRendered(createElExpression(sourceQuestion.getVisible()));
		}
	}
}
