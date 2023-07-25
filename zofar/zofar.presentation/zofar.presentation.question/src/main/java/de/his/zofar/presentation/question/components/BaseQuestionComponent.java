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
package de.his.zofar.presentation.question.components;
import javax.el.ValueExpression;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.service.evaluation.service.EvaluationService;
import de.his.zofar.service.question.model.structure.InstructionText;
import de.his.zofar.service.question.model.structure.IntroductionText;
import de.his.zofar.service.question.model.structure.QuestionText;
import de.his.zofar.service.question.model.structure.StructuredElement;
/**
 * This class sets all basic elements of a question.
 *
 * @author le
 *
 */
public abstract class BaseQuestionComponent extends HtmlPanelGroup{
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory
			.getLogger(BaseQuestionComponent.class);
	protected final static String BEAN_NAME = "surveyEngineModelBean";
	protected final static String INTRODUCTION_TEXT_CLASS = "question-introduction";
	protected final static String QUESTION_TEXT_CLASS = "question-text";
	protected final static String INSTRUCTION_TEXT_CLASS = "question-instruction";
	protected final static String ANSWER_OPTION_CONTAINER_CLASS = "answer-option-container";
	protected final static String ANSWER_OPTION_ITEM_CLASS = "answer-option-item";
	/**
     *
     */
	protected BaseQuestionComponent() {
		super();
		setLayout("block");
	}
	/**
	 * @param text
	 */
	void addInstruction(final InstructionText text) {
		this.addOutputText(text, INSTRUCTION_TEXT_CLASS);
	}
	/**
	 * @param text
	 */
	void addIntroduction(final IntroductionText text) {
		this.addOutputText(text, INTRODUCTION_TEXT_CLASS);
	}
	/**
	 * Adds text to the question component.
	 *
	 * @param text
	 * @param htmlClass
	 */
	protected void addOutputText(final StructuredElement text,
			final String htmlClass) {
		final HtmlPanelGroup textGroup = new HtmlPanelGroup();
		textGroup.setLayout("block");
		textGroup.setStyleClass(htmlClass);
		final HtmlOutputText outputText = new HtmlOutputText();
		outputText.setValueExpression("value",
				convertText((String) text.getContent()));
		outputText.setStyleClass(htmlClass);
		textGroup.getChildren().add(outputText);
		if (!text.getVisibilityCondition().isEmpty()) {
			textGroup.setValueExpression("rendered",
					createConditionExpression(text.getVisibilityCondition()));
		}
		getChildren().add(textGroup);
	}
	/**
	 * @param text
	 */
	void addQuestionText(final QuestionText text) {
		this.addOutputText(text, QUESTION_TEXT_CLASS);
	}
	/**
	 * @param condition
	 */
	void addVisibility(final String condition) {
		if (!condition.isEmpty()) {
			this.setValueExpression("rendered",
					createConditionExpression(condition));
		}
	}
	/**
	 * Creates a value expression that binds the input to a map value of the
	 * managed bean to save the values.
	 *
	 * @param variableName
	 * @param typeClass
	 * @return
	 */
	protected ValueExpression createValueExpressionForInput(
			final String variableName, final Class<?> typeClass) {
		final String elExpression = "#{" + BEAN_NAME + ".valueMap['"
				+ variableName + "']}";
		final ValueExpression ve = getFacesContext()
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(),
						elExpression, typeClass);
		return ve;
	}
	/**
	 * @param condition
	 * @return
	 */
	protected ValueExpression createConditionExpression(final String condition) {
		final String expression = "#{evaluatorBean.isVisible('" + condition
				+ "')}";
		final ValueExpression ve = getFacesContext()
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(),
						expression, Boolean.class);
		return ve;
	}
	protected ValueExpression convertText(final String text) {
		final String expression = "#{evaluatorBean.convertText('"
				+ EvaluationService.quote(text) + "')}";
		final ValueExpression ve = getFacesContext()
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(),
						expression, String.class);
		return ve;
	}
}
