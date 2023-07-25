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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.options;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author dick
 *
 */
@FacesRenderer(componentFamily = MultipleOption.COMPONENT_FAMILY, rendererType = ZofarVerticalMultipleOptionRenderer.RENDERER_TYPE)
public class ZofarVerticalMultipleOptionRenderer extends ZofarCheckboxOptionRenderer {
	public static final String RENDERER_TYPE = "org.zofar.VerticalMultipleOption";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarVerticalMultipleOptionRenderer.class);
	public ZofarVerticalMultipleOptionRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		Boolean isExclusice = (Boolean) component.getAttributes().get("exclusive");
		if (isExclusice == null) {
			isExclusice = false;
		} 
		if (isExclusice) {
			writer.writeAttribute("class", "custom-control custom-checkbox custom-checkbox-exclusive", null);
		} else {
			writer.writeAttribute("class", "custom-control custom-checkbox", null);
		}
	}
	protected void encodeItem(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final UIComponent parent = JsfUtility.getInstance().getParentResponseDomain(component);
		if (parent == null) {
			return;
		}
		String labelPosition = (String) parent.getAttributes().get("labelPosition");
		if (labelPosition == null) {
			labelPosition = ZofarResponseDomainRenderer.LABEL_POSITION_RIGHT;
		}
		Boolean showValues = (Boolean) parent.getAttributes().get("showValues");
		if (showValues == null) {
			showValues = false;
		}
		Boolean isMissing = (Boolean) component.getAttributes().get("missing");
		if (isMissing == null) {
			isMissing = false;
		}
		Boolean alignAttached = (Boolean) parent.getAttributes().get("alignAttached");
		if (alignAttached == null) {
			alignAttached = false;
		}
		String labelStr = JsfUtility.getInstance().getFieldAsString(context, component, "label");
		final String valueStr = JsfUtility.getInstance().getFieldAsString(context, component, "value");
		if (showValues && !isMissing) {
			labelStr = labelStr.trim();
			if (labelStr.equals(""))
				labelStr = valueStr;
			else
				labelStr = "(" + valueStr + ") " + labelStr;
		}
		String toolStr = (String) component.getAttributes().get("tooltipText");
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		writer.writeAttribute("data-label-pos", labelPosition, null);
		if (isMissing) {
			writer.writeAttribute("class", "checkbox-ismissing", null);
		}
		if (((ISequence.class).isAssignableFrom(component.getClass()))) {
			final Object sequenceId = ((ISequence) component).getSequenceId();
			if (sequenceId != null) {
				writer.writeAttribute("data-sequence", sequenceId, null);
			}
		}
		if (labelPosition.equals(ZofarResponseDomainRenderer.LABEL_POSITION_LEFT)) {
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "description", null);
			String classes = "custom-control-description";
			if (!((MultipleOption) component).isShowLabelFlag()) {
				classes = classes + " hidden-md-up";
			}
			writer.writeAttribute("class", classes, null);
			writer.startElement("span", component);
			writer.write("" + labelStr + "");
			writer.endElement("span");
			this.encodeOpenQuestion(context, component, alignAttached);
			writer.endElement("span");
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "indicator", null);
			writer.writeAttribute("class", "custom-control-indicator", null);
			writer.write(" ");
			writer.endElement("span");
			this.encodeInput(writer, context, component);
		} else {
			this.encodeInput(writer, context, component);
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "indicator", null);
			writer.writeAttribute("class", "custom-control-indicator", null);
			writer.write(" ");
			writer.endElement("span");
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "description", null);
			String classes = "custom-control-description";
			if(!((MultipleOption)component).isShowLabelFlag()){
				classes = classes + " hidden-md-up";
			}
			writer.writeAttribute("class", classes, null);
			writer.startElement("span", component);
			writer.write("" + labelStr + "");
			writer.endElement("span");
			this.encodeOpenQuestion(context, component, alignAttached);
			writer.endElement("span");
		}
		writer.endElement("label");
		if (toolStr!=null && !toolStr.equals("")) {
			writer.startElement("span", component);
			writer.writeAttribute("data-toggle", "tooltip", null);
			writer.writeAttribute("title", toolStr, null);
			writer.writeAttribute("class", "zo-tooltip", null);
				writer.startElement("img", component);
				writer.writeAttribute("class","zo-tooltip-image", null);
				writer.writeAttribute("src", "resources/html5/images/info.svg", null);
				writer.endElement("img");
			writer.endElement("span");
		} 
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final UIComponent parent = JsfUtility.getInstance().getParentResponseDomain(component);
		if (parent == null) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}
}
