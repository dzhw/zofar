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
package de.his.zofar.presentation.surveyengine.ui.renderer.display;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.display.UIDisplayContainer;

@FacesRenderer(componentFamily = UIDisplayContainer.COMPONENT_FAMILY, rendererType = ContainerRenderer.RENDERER_TYPE)
public class ContainerRenderer extends Renderer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContainerRenderer.class);

	public static final String RENDERER_TYPE = "org.zofar.display.container";

	public ContainerRenderer() {
		super();
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeBegin(context, component);
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if(component == null)return;
        if (!component.isRendered()) return;

		for (final UIComponent child : component.getChildren()) {
			child.encodeAll(context);
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeEnd(context, component);
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
