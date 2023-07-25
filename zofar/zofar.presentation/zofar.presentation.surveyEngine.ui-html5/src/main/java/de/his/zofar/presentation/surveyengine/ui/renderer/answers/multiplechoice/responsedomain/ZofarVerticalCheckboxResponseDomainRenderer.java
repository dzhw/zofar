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
import de.his.zofar.presentation.surveyengine.ui.renderer.container.UnitRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author dick
 *
 */
@FacesRenderer(componentFamily = MultipleChoiceResponseDomain.COMPONENT_FAMILY, rendererType = ZofarVerticalCheckboxResponseDomainRenderer.RENDERER_TYPE)
public class ZofarVerticalCheckboxResponseDomainRenderer extends ZofarMultipleChoiceResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.VerticalCheckboxResponseDomain";
	private static final String CSS_CONTAINER_CLASS = "zo-multiplechoice-vertical-responsedomain";
	private static final String CSS_UNIT_CLASS = "zo-matrix-unit";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarVerticalCheckboxResponseDomainRenderer.class);
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
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-orientation form-vertical", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-1-col", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "custom-form custom-form-checkbox", null);
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.
	 * FacesContext , javax.faces.component.UIComponent)
	 */
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		int rowIndex = 0;
		rowIndex = this.renderChildrenRecursiv(component.getChildren(), context, component, component, rowIndex);
	}
	private int renderChildrenRecursiv(final List<UIComponent> components, final FacesContext context,
			final UIComponent component, final UIComponent parent, int rowIndex) throws IOException {
		for (final UIComponent child : components) {
			if (MultipleOption.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderOption((MultipleOption) child, context, component, rowIndex);
			} else if (Section.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderUnit(component, (Section) child, context, rowIndex);	
			} else if (UISort.class.isAssignableFrom(child.getClass())) {
				rowIndex = this.renderChildrenRecursiv(((UISort) child).sortChildren(), context, component, parent,
						rowIndex);
			} else {
				rowIndex = this.renderChildrenRecursiv(child.getChildren(), context, component, parent, rowIndex);
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
	private int renderOption(final MultipleOption option, final FacesContext context, final UIComponent component,
			int rowIndex) throws IOException {
		option.encodeAll(context);
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
	private void renderUnitHeader(final UIComponent headerFacet, final FacesContext context) throws IOException {
		if (headerFacet == null) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("legend", headerFacet);
		if (UIText.class.isAssignableFrom(headerFacet.getClass())) {
			headerFacet.encodeAll(context);
		}
		writer.endElement("legend");
	}
	/**
	 * renders a unit in a the response domain. starts a new nested table.
	 *
	 * @param unit
	 *            the unit to render
	 * @param context
	 * @throws IOException
	 */
	private int renderUnit(final UIComponent component, final Section unit, final FacesContext context, int rowIndex)
			throws IOException {
		UnitRenderer unitRennderer = new UnitRenderer();
		unitRennderer.encodeBegin(context,unit);
		rowIndex = this.renderChildrenRecursiv(unit.getChildren(), context, component, unit, rowIndex);
		unitRennderer.encodeEnd(context,component);
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
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
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
