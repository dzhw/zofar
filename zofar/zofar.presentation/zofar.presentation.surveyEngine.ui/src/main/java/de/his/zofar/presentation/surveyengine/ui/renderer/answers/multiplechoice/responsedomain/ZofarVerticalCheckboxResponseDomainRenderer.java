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
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.MultipleOption;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.MultipleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.components.text.UIText;
/**
 * @author dick
 * 
 */
@FacesRenderer(componentFamily = MultipleChoiceResponseDomain.COMPONENT_FAMILY, rendererType = ZofarVerticalCheckboxResponseDomainRenderer.RENDERER_TYPE)
public class ZofarVerticalCheckboxResponseDomainRenderer extends
		ZofarMultipleChoiceResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.VerticalCheckboxResponseDomain";
	private static final String CSS_CONTAINER_CLASS = "zo-multiplechoice-vertical-responsedomain";
	private static final String CSS_UNIT_CLASS = "zo-matrix-unit";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarVerticalCheckboxResponseDomainRenderer.class);
	public ZofarVerticalCheckboxResponseDomainRenderer() {
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
		startTable(context.getResponseWriter(), component, CSS_CONTAINER_CLASS);
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
		int rowIndex = 0;
		rowIndex = renderChildrenRecursiv(component.getChildren(), context, component,component,rowIndex);
	}
	private synchronized int renderChildrenRecursiv(final List<UIComponent> components,
			final FacesContext context, UIComponent component, UIComponent parent,int rowIndex) throws IOException {
		for (final UIComponent child : components) {
			if (MultipleOption.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderOption((MultipleOption) child, context, component,rowIndex);
			} else if (Section.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderUnit(component,(Section) child, context,rowIndex);
			} else if (UISort.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderChildrenRecursiv(((UISort) child).sortChildren(),
						context, component,parent,rowIndex);
			} else {
				rowIndex = this.renderChildrenRecursiv(child.getChildren(), context,component,
						parent,rowIndex);
			}
		}
		return rowIndex;
	}
	/**
	 * renders one line of the response domain, the actual checkbox and the
	 * label.
	 * 
	 * @param option
	 *            the option to render
	 * @param context
	 * @throws IOException
	 */
	private synchronized int renderOption(final MultipleOption option,
			final FacesContext context, final UIComponent component,int rowIndex)
			throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		final String[] rowClasses = itemClassesToArray((String) component
				.getAttributes().get("itemClasses"));
		final boolean hasRowClasses = rowClasses.length > 0;
		writer.startElement("tr", option);
		if (hasRowClasses) {
			writer.writeAttribute("class", rowClasses[rowIndex++
					% rowClasses.length], null);
		}
		option.encodeAll(context);
		writer.endElement("tr");
		return rowIndex;
	}
	/**
	 * renders the header facet of a response domain unit.
	 * 
	 * @param headerFacet
	 *            the header facet to render
	 * @param context
	 * @throws IOException
	 */
	private synchronized void renderUnitHeader(final UIComponent headerFacet,
			final FacesContext context) throws IOException {
		if (headerFacet == null) {
			return;
		}
		if (UIText.class.isAssignableFrom(headerFacet.getClass())) {
			headerFacet.encodeAll(context);
		}
	}
	/**
	 * renders a unit in a the response domain. starts a new nested table.
	 * 
	 * @param unit
	 *            the unit to render
	 * @param context
	 * @throws IOException
	 */
	private synchronized int renderUnit(final UIComponent component, final Section unit, final FacesContext context,int rowIndex)
			throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("tr", unit);
		writer.startElement("td", unit);
		writer.writeAttribute("class", "zo-multiple-unit-header", null);
		writer.writeAttribute("style","padding-top:10px;padding-bottom:10px;",null);
		int count = 3;
		count=4;
		writer.writeAttribute("colspan", count, null);
		this.renderUnitHeader(unit.getFacet("header"), context);
		writer.endElement("td");
		writer.endElement("tr");
		rowIndex = this.renderChildrenRecursiv(unit.getChildren(), context,component,unit,rowIndex);
		return rowIndex;
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
		endTable(writer);
	}
}
