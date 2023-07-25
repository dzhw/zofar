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
import java.util.List;
import javax.el.ValueExpression;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import de.his.zofar.service.question.model.concrete.BooleanQuestion;
import de.his.zofar.service.question.model.structure.StructuredElement;
/**
 * this class represents the jsf view of a multiple choice question
 *
 * @author le
 *
 */
public class MultipleChoiceQuestionComponent extends BaseQuestionComponent {
    private final HtmlPanelGroup answerOptionContainer = new HtmlPanelGroup();
    /**
     * @param question
     */
    public MultipleChoiceQuestionComponent() {
        super();
        setStyleClass("multiple-choice-question");
    }
    /**
     * adds all single choice question to the answer option container
     *
     * @param questions
     */
    void addSingleChoiceQuestions(final List<BooleanQuestion> questions) {
        answerOptionContainer.setLayout("block");
        answerOptionContainer.setStyleClass(ANSWER_OPTION_CONTAINER_CLASS);
        int index = 0;
        for (final BooleanQuestion question : questions) {
            final HtmlPanelGroup answerOption = new HtmlPanelGroup();
            answerOption.setLayout("block");
            answerOption.setStyleClass(ANSWER_OPTION_ITEM_CLASS + index);
            final HtmlSelectBooleanCheckbox checkbox = new HtmlSelectBooleanCheckbox();
            checkbox.setId(question.getVariable().getName());
            checkbox.setValueExpression(
                    "value",
                    createValueExpressionForInput(question.getVariable()
                            .getName(), Boolean.class));
            final HtmlOutputLabel outputLabel = new HtmlOutputLabel();
            outputLabel.setFor(checkbox.getClientId());
            String label = "";
            for (final StructuredElement element : question.getHeader()) {
                label += element.getContent();
            }
            outputLabel.setValue(label);
            answerOption.getChildren().add(checkbox);
            answerOption.getChildren().add(outputLabel);
            if (!question.getVisibilityCondition().isEmpty()) {
                answerOption.setValueExpression("rendered",
                        createConditionExpression(question
                                .getVisibilityCondition()));
            }
            answerOptionContainer.getChildren().add(answerOption);
            index++;
        }
        getChildren().add(answerOptionContainer);
    }
	/**
	 * Creates a value expression that binds the input to a map value of the
	 * managed bean to save the values.
	 * 
	 * @param variableName
	 * @param typeClass
	 * @return
	 */
	@Override
	protected ValueExpression createValueExpressionForInput(
			final String variableName, final Class<?> typeClass) {
		final String elExpression = "#{" + BEAN_NAME + ".valueMap['"
				+ variableName + "'].answerValue}";
		final ValueExpression ve = getFacesContext()
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(),
						elExpression, typeClass);
		return ve;
	}
}
