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
package de.his.zofar.presentation.surveyengine.ui.components.text;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.text.ZofarResponseOptionDropDownRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.text.ZofarResponseOptionRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author le
 *
 */
@FacesComponent("org.zofar.text.ResponseOption")
public class UIResponseOptionText extends UIText {
	public static final String COMPONENT_FAMILY = "org.zofar.text.ResponseOption";
	private static final Logger LOGGER = LoggerFactory.getLogger(UIResponseOptionText.class);
	public UIResponseOptionText() {
		super();
	}
	@Override
	public String getRendererType() {
		final UIComponent parentRdc = JsfUtility.getInstance().getParentResponseDomain(this.getParent());
		String rendererType = null;
		if (parentRdc != null) {
			if (parentRdc instanceof UIDropDownResponseDomain) {
				rendererType = ZofarResponseOptionDropDownRenderer.RENDERER_TYPE;
			}else {
				rendererType = ZofarResponseOptionRenderer.RENDERER_TYPE;
			}
		}
		return rendererType;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.UIComponent#getFamily()
	 */
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.UIComponentBase#isTransient()
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
}
