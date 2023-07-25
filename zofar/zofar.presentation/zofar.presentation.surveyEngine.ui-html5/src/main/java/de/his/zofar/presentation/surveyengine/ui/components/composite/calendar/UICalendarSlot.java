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
package de.his.zofar.presentation.surveyengine.ui.components.composite.calendar;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;

import de.his.zofar.presentation.surveyengine.ui.renderer.calendar.CalendarSlotRenderer;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "de.his.zofar.Calendar.Slot")
public class UICalendarSlot extends UIInput implements NamingContainer {

	public static final String COMPONENT_FAMILY = "de.his.zofar.Calendar.Slot";

	public UICalendarSlot() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getRendererType() {
		return CalendarSlotRenderer.RENDERER_TYPE;
	}

}
