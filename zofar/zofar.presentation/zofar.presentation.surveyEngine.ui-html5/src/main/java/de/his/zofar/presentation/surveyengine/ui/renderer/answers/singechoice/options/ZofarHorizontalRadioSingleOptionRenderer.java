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
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = SingleOption.COMPONENT_FAMILY, rendererType = ZofarHorizontalRadioSingleOptionRenderer.RENDERER_TYPE)
public class ZofarHorizontalRadioSingleOptionRenderer extends ZofarRadioSingleOptionRenderer {
	public static final String RENDERER_TYPE = "org.zofar.HorizontalRadioSingleOption";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarHorizontalRadioSingleOptionRenderer.class);
	public ZofarHorizontalRadioSingleOptionRenderer() {
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
		String classes = "custom-control custom-radio";
		writer.writeAttribute("class", classes, null);
	}
	protected void encodeItem(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final UIComponent parent = JsfUtility.getInstance().getParentResponseDomain(component.getParent());
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
		if(showValues && !isMissing){
			labelStr = labelStr.trim();
			if(labelStr.equals(""))labelStr = valueStr;
			else labelStr = "("+valueStr+") "+labelStr;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("label", component);
		if(!((SingleOption)component).isMissing())writer.writeAttribute("class", "pipe-zofar-item", null);
		writer.writeAttribute("data-label-pos", labelPosition, null);
		if(((ISequence.class).isAssignableFrom(component.getClass()))){
			final Object sequenceId = ((ISequence)component).getSequenceId();
			if(sequenceId != null){
				writer.writeAttribute("data-sequence", sequenceId, null);
			}
		}
		String classes = "custom-control-description";
		if(!((SingleOption)component).isShowLabelFlag()){
			classes = classes + " hidden-md-up";
		}
		if(labelPosition.equals(ZofarResponseDomainRenderer.LABEL_POSITION_LEFT)){
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "description", null);
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
			this.encodeInput(writer, component);
		}
		else{
			this.encodeInput(writer, component);
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "indicator", null);
			writer.writeAttribute("class", "custom-control-indicator", null);
			writer.write(" ");
			writer.endElement("span");
			writer.startElement("span", component);
			writer.writeAttribute("data-missing", isMissing, null);
			writer.writeAttribute("data-label-type", "description", null);
			writer.writeAttribute("class", classes, null);
			writer.startElement("span", component);
			writer.write("" + labelStr + "");
			writer.endElement("span");
			this.encodeOpenQuestion(context, component, alignAttached);
			writer.endElement("span");
		}	
		writer.endElement("label");
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}
}
