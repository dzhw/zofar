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
/*
 *
 */
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.open.options;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.OpenOption;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;

/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIMatrixItem.COMPONENT_FAMILY, rendererType = ZofarOpenMatrixItemRenderer.RENDERER_TYPE)
public class ZofarOpenMatrixItemRenderer extends ZofarMatrixItemRenderer {

	public static final String RENDERER_TYPE = "org.zofar.OpenMatrixItem";

	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarOpenMatrixItemRenderer.class);

	public ZofarOpenMatrixItemRenderer() {
		super();
	}

	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeBegin(context, component);
		
		final UIComponent columns = component.getFacet("columns");
		if (columns != null) {
			List<UIComponent> columnItems = columns.getChildren();
			if(columnItems != null) {
				final int colCount = columnItems.size();
				int index = 0;
				for (final UIComponent child : component.getChildren()) {
					if((OpenOption.class).isAssignableFrom(child.getClass())) {
						if( index < colCount) {
							
							((OpenOption)child).getAttributes().put("zofar-column", columnItems.get(index));
							
						}
						index = index + 1;
					}
				}
			}

		}
	}
	
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("span", component);
		writer.writeAttribute("class", "openmatrix-rdc", null);
		super.encodeChildren(context, component);
		writer.endElement("span");
	}

	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		super.encodeEnd(context, component);
	}

}
