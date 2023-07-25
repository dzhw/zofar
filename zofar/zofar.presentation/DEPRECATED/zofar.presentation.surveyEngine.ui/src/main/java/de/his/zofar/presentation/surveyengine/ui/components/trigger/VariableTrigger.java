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
package de.his.zofar.presentation.surveyengine.ui.components.trigger;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.FacesComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator;
import de.his.zofar.presentation.surveyengine.ui.interfaces.Task;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 * 
 */
@FacesComponent(value = "org.zofar.VariableTrigger")
public class VariableTrigger extends AbstractTrigger implements Task {
	/**
     *
     */
	private static final long serialVersionUID = -1080328287743433867L;
	private static final Logger LOGGER = LoggerFactory.getLogger(VariableTrigger.class);
	public VariableTrigger() {
		super();
	}
	public Object triggerValue() {
		final Object value = getAttributes().get("value");
		return value;
	}
	public IAnswerBean variable() {
		final IAnswerBean value = (IAnswerBean) getAttributes().get("var");
		return value;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * de.his.zofar.surveyengine.uicomponents.trigger.AbstractTrigger#execute()
	 */
	private Object executeHelper(final boolean validDirection, final String condition, final IAnswerBean variable, final Object triggerValue) {
		final JsfUtility jsfUtility = JsfUtility.getInstance();
		final boolean conditionResult = jsfUtility.evaluateValueExpression(getFacesContext(), "#{" + condition + "}", Boolean.class);
		if (validDirection && conditionResult) {
			executeScriptItems();
			if (variable != null) {
				final String value = jsfUtility.evaluateValueExpression(getFacesContext(), "#{" + triggerValue + "}", Object.class) + "";
				variable.setStringValue(value);
			}
		}
		return null;
	}
	@Override
	public Object execute() {
		setNavigatorValue(getNavigatorAttribute());
		setDirectionValue(getDirectionAttribute());
		return executeHelper(validDirection(), getConditionAttribute(), variable(), triggerValue());
	}
	@Override
	public Map<String, Object> dump() {
		final Map<String, Object> dump = new HashMap<String, Object>();
		dump.put("navigator", getNavigatorAttribute());
		dump.put("condition", getConditionAttribute());
		dump.put("direction", getDirectionAttribute());
		dump.put("variable", variable());
		dump.put("value", triggerValue());
		return dump;
	}
	@Override
	public void executeTask(final Map<String, Object> dump) {
		setNavigatorValue((INavigator) dump.get("navigator"));
		setDirectionValue((String) dump.get("direction"));
		executeHelper(validDirection(), (String) dump.get("condition"), (IAnswerBean) dump.get("variable"), dump.get("value"));
	}
}
