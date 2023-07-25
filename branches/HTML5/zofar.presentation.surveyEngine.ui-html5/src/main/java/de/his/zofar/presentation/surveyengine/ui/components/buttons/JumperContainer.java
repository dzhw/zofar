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
package de.his.zofar.presentation.surveyengine.ui.components.buttons;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import de.his.zofar.presentation.surveyengine.ui.renderer.buttons.BreadCrumbNavRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.buttons.SideNavRenderer;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.JumperContainer")
public class JumperContainer extends UINamingContainer {

	public static final String COMPONENT_FAMILY = "org.zofar.JumperContainer";

	/**
	 *
	 */
	public JumperContainer() {
		super();
	}

	public String getSeparatorAttribute() {
		final String back = (String) this.getStateHelper().get("separator");
		if (back != null) {
			return back;
		}
		return "UNKOWN";
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getRendererType() {
		final String separatorKey = (String) this.getAttributes().get("separator");
		if (separatorKey != null) {
			if (separatorKey.equalsIgnoreCase("side")) {
				return SideNavRenderer.RENDERER_TYPE;
			}
			if (separatorKey.equalsIgnoreCase("top")) {
				return BreadCrumbNavRenderer.RENDERER_TYPE;
			}
		}
		return null;
	}
}
