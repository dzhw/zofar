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
package de.his.zofar.presentation.surveyengine.ui.components.buttons;

import java.util.ResourceBundle;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.ButtonPanel")
public class ButtonPanel extends UINamingContainer {

	private ResourceBundle bundle;

	public ButtonPanel() {
		super();
		this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.buttonPanel", FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}

	public ResourceBundle getBundle() {
		if ((this.bundle != null) && (!this.bundle.getLocale().equals(FacesContext.getCurrentInstance().getViewRoot().getLocale()))) {
			this.bundle = ResourceBundle.getBundle("de.his.zofar.messages.buttonPanel", FacesContext.getCurrentInstance().getViewRoot().getLocale());
		}
		return this.bundle;
	}

}
