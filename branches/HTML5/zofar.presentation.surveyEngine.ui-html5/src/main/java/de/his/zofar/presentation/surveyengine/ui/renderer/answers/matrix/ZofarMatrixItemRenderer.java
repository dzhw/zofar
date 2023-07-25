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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownMissingResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
public abstract class ZofarMatrixItemRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarMatrixItemRenderer.class);
	public ZofarMatrixItemRenderer() {
		super();
	}
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		boolean dropdown=false;
		for (final UIComponent child : component.getChildren()) {
			if((UIDropDownMissingResponseDomain.class).isAssignableFrom(child.getClass())){
				dropdown=true;
				break;
			}
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		String classes = "row highlight ";
		if(((ISequence.class).isAssignableFrom(component.getClass()))){
			final Object sequenceId = ((ISequence)component).getSequenceId();
			if(sequenceId != null){
				final int index = (Integer)sequenceId;
				if(index % 2 == 0)classes += "highlight-even ";
				else classes += "highlight-odd ";
				if(!dropdown) {
					classes += "carousel-item item";
				} else {
					classes += "item";
				}
				if(index == 0)classes += " active";
			}
		}
		writer.writeAttribute("class", classes, null);
		writer.writeAttribute("data-matrix", "item", null);
		writer.startElement("div", component);
		if(!dropdown) { 
			writer.writeAttribute("class", "col-md-4 hidden-sm-down", null);
		} else {
			writer.writeAttribute("class", "col-md-6 dropDownsmall", null);
		}
		final UIComponent header = component.getFacet("header");
		if (header != null) {
			writer.startElement("p", header);
			writer.writeAttribute("class", "text-sub", null);
			writer.writeAttribute("id", header.getClientId(context)+"_header", null);
			writer.write(JsfUtility.getInstance().getTextComponentAsString(context, header));
			for (final UIComponent child : component.getChildren()) {
				if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass())){
					child.encodeAll(context);
				}
			}
			writer.endElement("p");
		}
		writer.endElement("div");
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context
	 * .FacesContext)
	 */
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		for (final UIComponent child : component.getChildren()) {
			if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass()))continue;
			child.encodeAll(context);
		}
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
