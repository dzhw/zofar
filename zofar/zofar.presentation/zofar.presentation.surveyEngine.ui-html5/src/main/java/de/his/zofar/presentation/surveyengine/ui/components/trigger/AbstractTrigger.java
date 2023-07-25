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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator;
import de.his.zofar.presentation.surveyengine.ui.interfaces.Task;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 *
 * Parent class of all Triggers
 *
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.Trigger")
public abstract class AbstractTrigger extends UINamingContainer implements Task, Identificational, Visible {
	/**
	 *
	 */
	private static final long serialVersionUID = -6371800205059902605L;
	public static final String DIRECTION_SAME = "same";
	public static final String DIRECTION_BACKWARD = "backward";
	public static final String DIRECTION_FORWARD = "forward";
	public static final String DIRECTION_UNKOWN = "unkown";
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTrigger.class);
	private INavigator navigatorValue;
	private String directionValue;
	protected AbstractTrigger() {
		super();
	}
	protected void executeScriptItems() {
		final List<ScriptItem> items = this.getScriptItems();
		if (items != null) {
			this.evalScriptItems(items);
		}
	}
	/**
	 * @return the List of ScriptItems defined in Composite
	 */
	protected List<ScriptItem> getScriptItems() {
		if (this.getChildCount() == 0) {
			return null;
		}
		final Iterator<UIComponent> childIt = this.getChildren().iterator();
		final List<ScriptItem> back = new ArrayList<ScriptItem>();
		while (childIt.hasNext()) {
			final UIComponent child = childIt.next();
			if ((ScriptItem.class).isAssignableFrom(child.getClass())) {
				back.add((ScriptItem) child);
			}
		}
		return back;
	}
	/**
	 * evaluate given ScriptItems
	 *
	 * @param value
	 *            of ScriptItems
	 * @return
	 */
	protected void evalScriptItems(final List<ScriptItem> scriptItems) {
		if (scriptItems == null) {
			return;
		}
		final Iterator<ScriptItem> itemIt = scriptItems.iterator();
		while (itemIt.hasNext()) {
			this.evalScriptItem(itemIt.next());
		}
	}
	/**
	 * evaluate a given ScriptItem
	 *
	 * @param value
	 *            of ScriptItem
	 * @return
	 */
	protected void evalScriptItem(final ScriptItem scriptItem) {
		if (scriptItem == null) {
			return;
		}
		final FacesContext fc = FacesContext.getCurrentInstance();
		final JsfUtility jsfUtility = JsfUtility.getInstance();
		final Object result = jsfUtility.evaluateValueExpression(fc, "#{" + scriptItem.getValueAttribute() + "}", Object.class);
	}
	public abstract Object execute();
	/**
	 * @return the value of condition-Attribute defined in Composite
	 */
	public String getConditionAttribute() {
		String value = String.valueOf(this.getAttributes().get("condition"));
		if (value.equals("null")) {
			value = "true";
		}
		return value;
	}
	/**
	 * @return the value of direction-Attribute defined in Composite
	 */
	public String getDirectionAttribute() {
		String value = (String) this.getAttributes().get("direction");
		if (value == null) {
			value = DIRECTION_UNKOWN;
		}
		value = value.toLowerCase();
		value = value.trim();
		return value;
	}
	public String getDirectionValue() {
		return this.directionValue;
	}
	/**
	 * @return the value of navigator-Attribute defined in Composite
	 */
	public INavigator getNavigatorAttribute() {
		final INavigator value = (INavigator) this.getAttributes().get("navigator");
		return value;
	}
	public INavigator getNavigatorValue() {
		return this.navigatorValue;
	}
	public void setDirectionValue(final String direction) {
		this.directionValue = direction;
	}
	public void setNavigatorValue(final INavigator navigator) {
		this.navigatorValue = navigator;
	}
	/**
	 * @return true if given movement-Direction equals to the configured
	 *         direction
	 */
	protected boolean validDirection() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("navigator : {}", this.getNavigatorValue());
			LOGGER.debug("direction : {}", this.getDirectionValue());
		}
		if (this.getNavigatorValue() != null) {
			if ((this.getDirectionValue()).equals(DIRECTION_UNKOWN)) {
				return true;
			}
			if ((this.getDirectionValue()).equals(DIRECTION_FORWARD) && (this.getNavigatorValue()).isForward()) {
				return true;
			}
			if ((this.getDirectionValue()).equals(DIRECTION_BACKWARD) && (this.getNavigatorValue()).isBackward()) {
				return true;
			}
			if ((this.getDirectionValue()).equals(DIRECTION_SAME) && (this.getNavigatorValue()).isSame()) {
				return true;
			}
		}
		return false;
	}
}
