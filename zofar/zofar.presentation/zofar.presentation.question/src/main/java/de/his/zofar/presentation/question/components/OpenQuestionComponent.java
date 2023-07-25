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
import javax.faces.component.html.HtmlInputText;
import de.his.zofar.service.valuetype.model.NumberValueType;
import de.his.zofar.service.valuetype.model.StringValueType;
import de.his.zofar.service.valuetype.model.ValueType;
import de.his.zofar.service.valuetype.model.Variable;
/**
 * This class represents the JSF view of an open question.
 *
 * @author le
 *
 */
public class OpenQuestionComponent extends BaseQuestionComponent {
	/**
	 * @param question
	 */
	public OpenQuestionComponent() {
		super();
		setStyleClass("open-question");
	}
	/**
	 * Adds the actual HTML input to the question component.
	 *
	 * @param variable
	 */
	void addInputText(final Variable variable) {
		if (variable == null) {
			throw new IllegalArgumentException("variable cannot be null");
		}
		if (variable.getValueType() == null) {
			throw new IllegalArgumentException(
					"value type of a variable cannot be null");
		}
		final HtmlInputText inputText = new HtmlInputText();
		final ValueType valueType = variable.getValueType();
		if (StringValueType.class.isAssignableFrom(valueType.getClass())) {
			final StringValueType stringValue = (StringValueType) valueType;
			inputText.setMaxlength(stringValue.getLength());
			inputText.setRequired(!stringValue.getCanBeEmpty());
			inputText.setValueExpression(
					"value",
					createValueExpressionForInput(variable.getName(),
							String.class));
		} else if (NumberValueType.class.isAssignableFrom(valueType.getClass())) {
			inputText.setValueExpression(
					"value",
					createValueExpressionForInput(variable.getName(),
							Long.class));
		} else {
			throw new RuntimeException("Not yet implemented. "
					+ valueType.getClass());
		}
		getChildren().add(inputText);
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
