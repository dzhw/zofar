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
package de.his.zofar.presentation.surveyengine.ui.components.trigger.validators.constraints;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 *
 * Class to throw a ValidatorException,when validationCondition result to false
 *
 * @author meisner
 * @see javax.faces.validator.Validator
 */
@FacesValidator("org.zofar.CustomConstraintsValidator")
public class CustomConstraintsValidator implements Validator {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomConstraintsValidator.class);
	private String message = null;
	public void setMessage(final String message) {
		this.message = message;
	}
	@Override
	public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException {
		boolean flag = false;
		if (value != null) {
			if ((Boolean.class).isAssignableFrom(value.getClass())) {
				flag = (Boolean) value;
			}
			if ((String.class).isAssignableFrom(value.getClass())) {
				final JsfUtility jsfUtility = JsfUtility.getInstance();
				flag = jsfUtility.evaluateValueExpression(context, "#{" + ((String) value) + "}", Boolean.class);
			}
		}
		if (!flag) {
			final FacesMessage msg = new FacesMessage(this.message);
			throw new ValidatorException(msg);
		}
	}
}
