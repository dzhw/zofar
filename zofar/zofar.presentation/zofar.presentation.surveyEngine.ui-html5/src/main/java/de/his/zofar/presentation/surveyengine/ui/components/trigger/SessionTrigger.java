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
package de.his.zofar.presentation.surveyengine.ui.components.trigger;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator;
import de.his.zofar.presentation.surveyengine.ui.interfaces.Task;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.SessionTrigger")
public class SessionTrigger extends AbstractTrigger implements Task {
	/**
	 *
	 */
	private static final long serialVersionUID = -1424545977257685364L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionTrigger.class);
	public SessionTrigger() {
		super();
	}
	public String triggerTimeout() {
		final String value = (String) this.getAttributes().get("timeout");
		return value;
	}
	private Object executeHelper(final boolean validDirection, final String condition, final String timeout) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("navigatorBean :  {} ", this.getNavigatorValue());
			LOGGER.debug("validDirection :  {} ", validDirection);
			LOGGER.debug("condition :  {} ", condition);
			LOGGER.debug("timeout :  {} ", timeout);
		}
		final JsfUtility jsfUtility = JsfUtility.getInstance();
		if (validDirection && jsfUtility.evaluateValueExpression(this.getFacesContext(), "#{" + condition + "}", Boolean.class)) {
			this.executeScriptItems();
			HttpSession session = null;
			final FacesContext context = FacesContext.getCurrentInstance();
			if (context != null) {
				final ExternalContext externalContext = context.getExternalContext();
				if (externalContext != null) {
					final HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
					if (request != null) {
						session = request.getSession();
					}
				}
			}
			if (session != null) {
				final String value = jsfUtility.evaluateValueExpression(this.getFacesContext(), "#{" + timeout + "}", Object.class) + "";
				final int interval = (new Double(value)).intValue();
				session.setMaxInactiveInterval(interval);
			}
		}
		return null;
	}
	@Override
	public Object execute() {
		this.setNavigatorValue(this.getNavigatorAttribute());
		this.setDirectionValue(this.getDirectionAttribute());
		return this.executeHelper(this.validDirection(), this.getConditionAttribute(), this.triggerTimeout());
	}
	@Override
	public Map<String, Object> dump() {
		final Map<String, Object> dump = new HashMap<String, Object>();
		dump.put("navigator", this.getNavigatorAttribute());
		dump.put("condition", this.getConditionAttribute());
		dump.put("direction", this.getDirectionAttribute());
		dump.put("timeout", this.triggerTimeout());
		return dump;
	}
	@Override
	public void executeTask(final Map<String, Object> dump) {
		this.setNavigatorValue((INavigator) dump.get("navigator"));
		this.setDirectionValue((String) dump.get("direction"));
		this.executeHelper(this.validDirection(), (String) dump.get("condition"), dump.get("timeout") + "");
	}
}
