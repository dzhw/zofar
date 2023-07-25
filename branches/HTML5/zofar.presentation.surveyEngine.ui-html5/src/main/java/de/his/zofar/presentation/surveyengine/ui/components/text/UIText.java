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

import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlPanelGroup;

import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.renderer.text.ZofarTextRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * base component for all text components.
 *
 * @author le
 *
 */
public abstract class UIText extends UINamingContainer implements Identificational, Visible {

	public static final String COMPONENT_FAMILY = "org.zofar.Text";

	public UIText() {
		super();
	}

	public String getContent() {
		final HtmlPanelGroup panel = (HtmlPanelGroup) this.getFacet(COMPOSITE_FACET_NAME).getChildren().get(0);
		String content;
		if (this.isRendered()) {
			content = panel.getChildren().get(0).toString();
		} else {
			content = "";
		}

		return content;
	}
	
	public boolean getContainerAttribute(){
		return Boolean.parseBoolean(JsfUtility.getInstance().getAttribute(this,"container", true+""));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.UIComponentBase#getRendererType()
	 */
	@Override
	public String getRendererType() {
		return ZofarTextRenderer.RENDERER_TYPE;
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
