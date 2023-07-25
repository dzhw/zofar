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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;

@FacesRenderer(componentFamily = SingleOption.COMPONENT_FAMILY, rendererType = ZofarComparisonRadioSingleChoiceOptionRenderer.RENDERER_TYPE)
public class ZofarComparisonRadioSingleChoiceOptionRenderer extends
		ZofarRadioSingleOptionRenderer {
	
	public static final String RENDERER_TYPE = "org.zofar.ComparisonRadioSingleOption";
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarComparisonRadioSingleChoiceOptionRenderer.class);
	
	public ZofarComparisonRadioSingleChoiceOptionRenderer() {
		super();
	}
	
	@Override
	public synchronized boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public synchronized void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
		encodeInput(context, component);
	}
	
	@Override
	public synchronized void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
		encodeOpenQuestion(context, component);
	}
	
	@Override
	public synchronized void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}
	}
}
