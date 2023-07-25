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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = RadioButtonSingleChoiceResponseDomain.COMPONENT_FAMILY, rendererType = ZofarVerticalUnitRadioResponseDomainRenderer.RENDERER_TYPE)
public class ZofarVerticalUnitRadioResponseDomainRenderer extends ZofarVerticalRadioResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.VerticalUnitRadioResponseDomain";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarVerticalUnitRadioResponseDomainRenderer.class);
	public ZofarVerticalUnitRadioResponseDomainRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-orientation form-vertical", null);
		writer.writeAttribute("id", component.getClientId(context), null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-1-col", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "flex-wrapper", null);
	}
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		((RadioButtonSingleChoiceResponseDomain)component).sequenceAnswerOptions();
		final ResponseWriter writer = context.getResponseWriter();
		for (final UIComponent child : component.getChildren()) {
			if (!child.isRendered()) {
				continue;
			}
			if ((Section.class).isAssignableFrom(child.getClass())) {
				((Section)child).setInjectedClasses("custom-form custom-form-radio");
				child.encodeAll(context);
			} else if ((UISort.class).isAssignableFrom(child.getClass())) {
				for (final UIComponent sortChild : ((UISort) child).sortChildren()) {
					if (!sortChild.isRendered()) {
						continue;
					}
					sortChild.encodeAll(context);
				}
			} else {
				writer.startElement("div", component);
				writer.writeAttribute("class", "custom-form custom-form-radio", null);
				child.encodeAll(context); 
				writer.endElement("div");
			}
		}
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
}
