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
package de.his.zofar.presentation.surveyengine.provider;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.util.JsfUtility;
/**
 * Bean to provide EL - Support in ResourceBundle
 *
 * @author meisner
 *
 */
@ManagedBean(name = "msgs")
@SessionScoped
public class MessageProvider extends HashMap<Object, Object> implements
		Serializable {
	private static final long serialVersionUID = -1077163456237674734L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MessageProvider.class);
	public MessageProvider() {
		super();
	}
	@Override
	public Object get(final Object key) {
		String msg = JsfUtility.getInstance().evaluateValueExpression(
				FacesContext.getCurrentInstance(),
				"#{msgbundle['" + key + "']}", String.class);
		if ((msg != null) && ((String.class).isAssignableFrom(msg.getClass()))) {
			msg = JsfUtility.getInstance().evaluateValueExpression(
					FacesContext.getCurrentInstance(), msg + "", String.class);
		}
		return msg;
	}
}
