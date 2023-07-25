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
package de.his.zofar.presentation.surveyengine.ui.renderer.text;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import de.his.zofar.presentation.surveyengine.ui.components.text.UIResponseOptionText;

@FacesRenderer(componentFamily = UIResponseOptionText.COMPONENT_FAMILY, rendererType = ZofarResponseOptionDropDownRenderer.RENDERER_TYPE)
public class ZofarResponseOptionDropDownRenderer extends ZofarTextRenderer {
	
	public static final String RENDERER_TYPE = "org.zofar.text.DropDownResponseOption";
	
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.render.Renderer#getRendersChildren()
	 */
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		super.encodeBegin(context, component);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.
	 * FacesContext , javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
		super.encodeChildren(context, component);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		super.encodeEnd(context, component);
		final ResponseWriter writer = context.getResponseWriter();
	}


}
