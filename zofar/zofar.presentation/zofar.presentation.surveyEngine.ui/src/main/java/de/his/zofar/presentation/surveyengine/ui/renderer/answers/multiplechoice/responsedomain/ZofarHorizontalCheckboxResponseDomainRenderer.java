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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.multiplechoice.responsedomain;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.MultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
/**
 * @author dick	
 * 
 */
@FacesRenderer(componentFamily = MultipleChoiceResponseDomain.COMPONENT_FAMILY, rendererType = ZofarHorizontalCheckboxResponseDomainRenderer.RENDERER_TYPE)
public class ZofarHorizontalCheckboxResponseDomainRenderer extends
		ZofarMultipleChoiceResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.HorizontalCheckboxResponseDomain";
	private int row;
	public ZofarHorizontalCheckboxResponseDomainRenderer() {
		super();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public synchronized void encodeBegin(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		row = 0;
		final ResponseWriter writer = context.getResponseWriter();
		String additonalClasses = null;
		final boolean separateMissings = (Boolean) component.getAttributes()
				.get("missingSeparated");
		if (separateMissings)
			additonalClasses = "zo-missingSeparated zo-missingSeparated-horizontal";
		startTable(context.getResponseWriter(), component, additonalClasses);
		writer.startElement("tr", component);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent)
	 */
	@Override
	public synchronized void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		for (final UIComponent child : component.getChildren()) {
			if (!child.isRendered()) {
				continue;
			}
			encodeChildrenHelper(context, component, child);
		}
	}
	public synchronized void encodeChildrenHelper(final FacesContext context,
			final UIComponent component, final UIComponent child)
			throws IOException {
		if (!child.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		if ((Section.class).isAssignableFrom(child.getClass())) {
			writer.startElement("td", component);
			final String labelPosition = (String) component.getAttributes().get("labelPosition");
			if (labelPosition != null) {
				if (labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
					writer.writeAttribute("valign", "top", null);
				} else {
					writer.writeAttribute("valign", "bottom", null);
				}
			}
			writer.startElement("table", component);
			writer.writeAttribute("cellspacing", 0, null);
			writer.writeAttribute("cellpadding", 0, null);
			writer.writeAttribute("border", 0, null);
			writer.startElement("tr", component);
			writer.startElement("td", component);
			writer.writeAttribute("colspan", child.getChildCount(), null);
			writer.writeAttribute("align", "center", null);
			child.encodeAll(context);
			writer.endElement("td");
			writer.endElement("tr");
			writer.startElement("tr", component);
			for (final UIComponent sectionChild : child.getChildren()) {
				encodeChildrenHelper(context, component, sectionChild);
			}
			writer.endElement("tr");
			writer.endElement("table");
			writer.endElement("td");
		} else if ((UISort.class).isAssignableFrom(child.getClass())) {
			for (final UIComponent sortChild : ((UISort) child).sortChildren()) {
				encodeChildrenHelper(context, component, sortChild);
			}
		} else {
			final String labelPosition = (String) component.getAttributes().get("labelPosition");
			final String[] rowClasses = itemClassesToArray((String) component
					.getAttributes().get("itemClasses"));
			final boolean hasRowClasses = rowClasses.length > 0;
			Boolean isMissing = (Boolean) child.getAttributes().get("missing");
			if (isMissing == null)
				isMissing = false;
			writer.startElement("td", component);
			if (labelPosition != null) {
				if (labelPosition
						.equals(ZofarResponseDomainRenderer.LABEL_POSITION_BOTTOM)) {
					writer.writeAttribute("valign", "top", null);
				} else {
					writer.writeAttribute("valign", "bottom", null);
				}
			}
			writer.startElement("table", component);
			writer.writeAttribute("width", "150px", null);
			String classes = "";
			if (hasRowClasses) {
				classes += rowClasses[row++ % rowClasses.length] + " ";
			}
			if (isMissing) {
				classes += "zo-ao-missing zo-ao-missing-horizontal ";
			}
			if (!classes.equals(""))
				writer.writeAttribute("class", classes.trim(), null);
			child.encodeAll(context);
			writer.endElement("table");
			writer.endElement("td");
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	@Override
	public synchronized void encodeEnd(final FacesContext context,
			final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("tr");
		endTable(writer);
	}
}
