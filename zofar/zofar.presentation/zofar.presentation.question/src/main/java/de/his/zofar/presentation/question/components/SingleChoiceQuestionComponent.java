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
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.convert.Converter;
import org.apache.myfaces.component.html.ext.HtmlSelectOneRadio;
import org.apache.myfaces.custom.radio.HtmlRadio;
import de.his.zofar.service.question.model.components.AnswerOption;
import de.his.zofar.service.question.model.concrete.SingleChoiceQuestion;
import de.his.zofar.service.question.model.interfaces.Answer;
/**
 * This class represents the JSF view of a single choice question.
 * 
 * @author le
 * 
 */
public class SingleChoiceQuestionComponent extends BaseQuestionComponent {
	private boolean dropDown = false;
	/**
     *
     */
	private final String LAYOUT = "spread";
	/**
	 * @param question
	 */
	public SingleChoiceQuestionComponent() {
		super();
		setStyleClass("single-choice-question");
	}
	/**
	 * Adds all answer options from the single choice question.
	 * 
	 * @param question
	 */
	void addAnswerOptions(final SingleChoiceQuestion question) {
		HtmlPanelGroup dataList = null;
		UIComponent answerOptionContainer = null;
		if (this.dropDown) {
			answerOptionContainer = new HtmlSelectOneMenu();
		} else {
			answerOptionContainer = new HtmlSelectOneRadio();
			((HtmlSelectOneRadio) answerOptionContainer).setLayout(LAYOUT);
		}
		answerOptionContainer.setId(question.getVariable().getName());
		answerOptionContainer.setValueExpression(
				"value",
				createValueExpressionForInput(question.getVariable().getName(),
						Answer.class));
		final String elExpression = "#{answerConverterBean}";
		final ValueExpression ve = getFacesContext()
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(),
						elExpression, Converter.class);
		answerOptionContainer.setValueExpression("converter", ve);
		if (question.getAnswerOptions() != null) {
			int rowIndex = 0;
			if (!dropDown) {
				dataList = new HtmlPanelGroup();
				dataList.setLayout("block");
				dataList.setStyleClass(ANSWER_OPTION_CONTAINER_CLASS);
			}
			for (final AnswerOption option : question.getAnswerOptions()) {
				final UISelectItem selectItem = new UISelectItem();
				selectItem.setItemValue(option);
				selectItem.setItemLabel(option.getDisplayText());
				answerOptionContainer.getChildren().add(selectItem);
				if (!dropDown) {
					final HtmlPanelGroup listItemPanel = new HtmlPanelGroup();
					listItemPanel.setLayout("block");
					listItemPanel.setStyleClass(ANSWER_OPTION_ITEM_CLASS
							+ rowIndex);
					final HtmlRadio radio = new HtmlRadio();
					radio.setFor(answerOptionContainer.getClientId());
					radio.setIndex(rowIndex);
					listItemPanel.getChildren().add(radio);
					if (option.getOpenQuestion() != null && !dropDown) {
						final BaseQuestionComponent oo = OpenQuestionComponentFactory
								.getInstance().createQuestionComponent(
										option.getOpenQuestion());
						oo.setLayout("");
						listItemPanel.getChildren().add(oo);
					}
					if (!option.getVisibilityCondition().isEmpty()) {
						listItemPanel.setValueExpression("rendered",
								createConditionExpression(option
										.getVisibilityCondition()));
					}
					dataList.getChildren().add(listItemPanel);
					rowIndex++;
				}
			}
		}
		getChildren().add(answerOptionContainer);
		if (!dropDown) {
			getChildren().add(dataList);
		}
	}
	/**
	 * If set to true then the component is rendered as a menu list otherwise as
	 * radio buttons. Default is false.
	 * 
	 * @param dropDown
	 *            the dropDown to set
	 */
	void setDropDown(final boolean dropDown) {
		this.dropDown = dropDown;
	}
}
