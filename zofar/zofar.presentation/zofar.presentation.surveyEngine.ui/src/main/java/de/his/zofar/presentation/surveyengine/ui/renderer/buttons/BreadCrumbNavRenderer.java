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
package de.his.zofar.presentation.surveyengine.ui.renderer.buttons;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.buttons.Jumper;
import de.his.zofar.presentation.surveyengine.ui.components.buttons.JumperContainer;

/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = JumperContainer.COMPONENT_FAMILY, rendererType = BreadCrumbNavRenderer.RENDERER_TYPE)
public class BreadCrumbNavRenderer extends Renderer {

	public static final String RENDERER_TYPE = "org.zofar.BreadCrumbNav";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BreadCrumbNavRenderer.class);

	public BreadCrumbNavRenderer() {
		super();
	}

	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered())
			return;

		this.encodeChildrenHelper(context, component, component.getChildren());
	}

	private void encodeChildrenHelper(final FacesContext context,
			final UIComponent component, final List<UIComponent> children) throws IOException {
		if (!component.isRendered())
			return;
		final ResponseWriter writer = context.getResponseWriter();
		final Iterator<UIComponent> it = children.iterator();
		while (it.hasNext()) {
			final UIComponent child = it.next();
			if (Jumper.class.isAssignableFrom(child.getClass())) {
				child.encodeAll(context);
				if (it.hasNext()) {
					writer.write("&nbsp;&gt;&nbsp;");
				}
			}

		}
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

}
