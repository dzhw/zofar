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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;

/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = MultipleOption.COMPONENT_FAMILY, rendererType = ZofarComparisonCheckboxOptionRenderer.RENDERER_TYPE)
public class ZofarComparisonCheckboxOptionRenderer extends
		ZofarCheckboxOptionRenderer {

	public static final String RENDERER_TYPE = "org.zofar.ComparisonMultipleOption";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarComparisonCheckboxOptionRenderer.class);

	public ZofarComparisonCheckboxOptionRenderer() {
		super();
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
		
		encodeInput(context, component);
	}

	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
		encodeOpenQuestion(context, component);
	}

	@Override
	public void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
	}
}
