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
package de.his.zofar.presentation.surveyengine.ui.components.common;

import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import de.his.zofar.presentation.surveyengine.ui.renderer.Progress.ProgressRenderer;

@FacesComponent("org.zofar.picturebar")
public class PictureBar extends UINamingContainer {

	public static final String COMPONENT_FAMILY = "org.zofar.picturebar";

	public PictureBar() {
		super();
	}

	@Override
	public String getRendererType() {
		return ProgressRenderer.RENDERER_TYPE;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public boolean isTransient() {
		return true;
	}

	public Integer getValueAttribute() {
		final Integer value = (Integer) this.getAttributes().get("value");
		return value;
	}

	public String getDotAttribute() {
		final String value = (String) this.getAttributes().get("dot");
		return value;
	}

	public String getArrowAttribute() {
		final String value = (String) this.getAttributes().get("arrow");
		return value;
	}

	public Map<?, ?> getMilestonesAttribute() {
		final Map<?, ?> value = (Map<?, ?>) this.getAttributes().get("milestones");
		return value;
	}

	public de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator getNavigatorAttribute() {
		final de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator value = (de.his.zofar.presentation.surveyengine.ui.interfaces.INavigator) this.getAttributes().get("navigator");
		return value;
	}

}
