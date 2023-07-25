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
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
@FacesRenderer(componentFamily = MultipleOption.COMPONENT_FAMILY, rendererType = ZofarHorizontalMultipleOptionRenderer.RENDERER_TYPE)
public class ZofarHorizontalMultipleOptionRenderer extends
		ZofarCheckboxOptionRenderer{
	public static final String RENDERER_TYPE = "org.zofar.HorizontalMultipleOption";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarHorizontalMultipleOptionRenderer.class);
	private final String inputClasses = "zo-ao-input zo-ao-input-horizontal ";
	private final String valueClasses = "zo-ao-value zo-ao-value-horizontal ";
	private final String labelClasses = "zo-ao-label zo-ao-label-horizontal ";
	private final String openClasses = "zo-ao-attached zo-ao-attached-horizontal ";
	public ZofarHorizontalMultipleOptionRenderer() {
		super();
	}
	@Override
	public synchronized boolean getRendersChildren() {
		return true;
	}
	@Override
	public synchronized void encodeBegin(final FacesContext context, final UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
			return;
        }
        Boolean alignAttached = (Boolean) parent.getAttributes().get("alignAttached");
		if (alignAttached == null)alignAttached = false;
		final boolean valueLabelFlag = hasValueLabel(context, component);
		boolean labelFlag = hasLabel(context, component);
		boolean openFlag = hasOpenQuestion(context, component);
		if(alignAttached){
			labelFlag = true;
			openFlag = true;
		}
		boolean skipAll = true;
		if (valueLabelFlag)
			skipAll = false;
		if (labelFlag)
			skipAll = false;
		if (openFlag)
			skipAll = false;
		final String labelPosition = (String) parent.getAttributes().get("labelPosition");
        Boolean isMissing = (Boolean) component.getAttributes().get("missing");
		if (isMissing == null)isMissing = false;
		final ResponseWriter writer = context.getResponseWriter();
		if ((labelPosition != null)
				&& labelPosition.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", inputClasses, null);
			writer.writeAttribute("align", "center", null);
			encodeInput(context, component);
			writer.endElement("td");
			writer.endElement("tr");
			if (skipAll) {
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.endElement("td");
				writer.endElement("tr");
			} else {
			}
		} else {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", labelClasses, null);
			writer.writeAttribute("align", "center", null);
			if (labelFlag) {
				encodeLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", valueClasses, null);
			writer.writeAttribute("align", "center", null);
			if (valueLabelFlag) {
				encodeValueLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
		}
	}
	@Override
	public synchronized void encodeChildren(final FacesContext context, final UIComponent component)
			throws IOException {
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
			return;
        }
        Boolean alignAttached = (Boolean) parent.getAttributes().get("alignAttached");
		if (alignAttached == null)alignAttached = false;
		boolean openFlag = hasOpenQuestion(context, component);
		if(alignAttached){
			openFlag = true;
		}
	}
	@Override
	public synchronized void encodeEnd(final FacesContext context, final UIComponent component)
			throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
        final UIComponent parent = (UIComponent) component.getAttributes().get(
                "parentResponseDomain");
        if (parent == null) {
			return;
        }
        Boolean alignAttached = (Boolean) parent.getAttributes().get("alignAttached");
		if (alignAttached == null)alignAttached = false;
		LOGGER.debug("alignAttached {}",alignAttached);
		final boolean valueLabelFlag = hasValueLabel(context, component);
		boolean labelFlag = hasLabel(context, component);
		boolean openFlag = hasOpenQuestion(context, component);
		if(alignAttached){
			labelFlag = true;
			openFlag = true;
		}
		boolean skipAll = true;
		if (valueLabelFlag)
			skipAll = false;
		if (labelFlag)
			skipAll = false;
		if (openFlag)
			skipAll = false;
		final String labelPosition = (String) parent.getAttributes().get("labelPosition");
		if ((labelPosition != null)
				&& labelPosition.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", valueClasses, null);
			writer.writeAttribute("align", "center", null);
			if (valueLabelFlag) {
				encodeValueLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "zo-ao-label", null);
			writer.writeAttribute("align", "center", null);
			if (labelFlag) {
				encodeLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
		} else {
			if (skipAll) {
				writer.startElement("tr", component);
				writer.startElement("td", component);
				writer.endElement("td");
				writer.endElement("tr");
			} else {
			}
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", inputClasses, null);
			writer.writeAttribute("align", "center", null);
			encodeInput(context, component);
			if (openFlag) {
				encodeOpenQuestion(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
		}
	}
}
