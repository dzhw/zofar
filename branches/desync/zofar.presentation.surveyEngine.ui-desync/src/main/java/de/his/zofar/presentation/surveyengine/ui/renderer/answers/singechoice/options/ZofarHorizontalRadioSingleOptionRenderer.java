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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.options;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.SingleOption;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
@FacesRenderer(componentFamily = SingleOption.COMPONENT_FAMILY, rendererType = ZofarHorizontalRadioSingleOptionRenderer.RENDERER_TYPE)
public class ZofarHorizontalRadioSingleOptionRenderer extends
		ZofarRadioSingleOptionRenderer {
	public static final String RENDERER_TYPE = "org.zofar.HorizontalRadioSingleOption";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarHorizontalRadioSingleOptionRenderer.class);
	private final static String INPUTCLASSES = "zo-ao-input zo-ao-input-horizontal";
	private final static String VALUECLASSES = "zo-ao-value zo-ao-value-horizontal";
	private final static String LABELCLASSES = "zo-ao-label zo-ao-label-horizontal";
	private final static String OPENCLASSES = "zo-ao-attached zo-ao-attached-horizontal ";
	public ZofarHorizontalRadioSingleOptionRenderer() {
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
		final UIComponent parent = (UIComponent) component.getAttributes().get(
				"parentResponseDomain");
		if (parent == null) {
			return;
		}
		Boolean alignAttached = (Boolean) parent.getAttributes().get(
				"alignAttached");
		if (alignAttached == null)
			alignAttached = false;
		final boolean valueLabelFlag = this.hasValueLabel(context, component);
		boolean labelFlag = this.hasLabel(context, component);
		boolean openFlag = this.hasOpenQuestion(context, component);
		if (alignAttached) {
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
		final String labelPosition = (String) parent.getAttributes().get(
				"labelPosition");
		Boolean isMissing = (Boolean) component.getAttributes().get("missing");
		if (isMissing == null) {
			isMissing = false;
		}
		final ResponseWriter writer = context.getResponseWriter();
		if ((labelPosition != null)
				&& labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", ZofarHorizontalRadioSingleOptionRenderer.INPUTCLASSES, null);
			writer.writeAttribute("align", "center", null);
			this.encodeInput(context, component);
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
			writer.writeAttribute("class", ZofarHorizontalRadioSingleOptionRenderer.LABELCLASSES, null);
			writer.writeAttribute("align", "center", null);
			if (labelFlag) {
				this.encodeLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", ZofarHorizontalRadioSingleOptionRenderer.VALUECLASSES, null);
			writer.writeAttribute("align", "center", null);
			if (valueLabelFlag) {
				this.encodeValueLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
		}
	}
	@Override
	public void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
	}
	@Override
	public void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent parent = (UIComponent) component.getAttributes().get(
				"parentResponseDomain");
		if (parent == null) {
			return;
		}
		Boolean alignAttached = (Boolean) parent.getAttributes().get(
				"alignAttached");
		if (alignAttached == null)
			alignAttached = false;
		Boolean isMissing = (Boolean) component.getAttributes().get("missing");
		if (isMissing == null) {
			isMissing = false;
		} 
		final boolean valueLabelFlag = this.hasValueLabel(context, component);
		boolean labelFlag = this.hasLabel(context, component);
		boolean openFlag = this.hasOpenQuestion(context, component);
		if (alignAttached) {
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
		final String labelPosition = (String) parent.getAttributes().get(
				"labelPosition");
		if ((labelPosition != null)
				&& labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", ZofarHorizontalRadioSingleOptionRenderer.VALUECLASSES, null);
			writer.writeAttribute("align", "center", null);
			if (valueLabelFlag) {
				this.encodeValueLabel(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("class", "zo-ao-label", null);
			writer.writeAttribute("align", "center", null);
			if (labelFlag) {
				this.encodeLabel(context, component);
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
			writer.writeAttribute("class", ZofarHorizontalRadioSingleOptionRenderer.INPUTCLASSES, null);
			writer.writeAttribute("align", "center", null);
			this.encodeInput(context, component);
			if (openFlag) {
				this.encodeOpenQuestion(context, component);
			}
			writer.endElement("td");
			writer.endElement("tr");
		}
	}
}
