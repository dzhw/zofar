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
package de.his.zofar.presentation.surveyengine.ui.renderer.container;
import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.RadioButtonSingleChoiceResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain.UIDropDownResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.common.UISort;
import de.his.zofar.presentation.surveyengine.ui.components.container.Section;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = Section.COMPONENT_FAMILY, rendererType = UnitRenderer.RENDERER_TYPE)
public class UnitRenderer extends Renderer {
	public static final String RENDERER_TYPE = "org.zofar.Unit";
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UnitRenderer.class);
	public UnitRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		LOGGER.info("begin {}", component.getClientId());
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent rdc = JsfUtility.getInstance().getParentResponseDomain(component);
		if ((rdc != null) && ((UIDropDownResponseDomain.class).isAssignableFrom(rdc.getClass()))) {
			writer.startElement("optgroup", component);
			writer.writeAttribute("id", component.getClientId(context), null);
			final UIComponent header = component.getFacet("header");
			if ((header != null) && (header.isRendered())) {
				writer.writeAttribute("label", JsfUtility.getInstance().getTextComponentAsString(context,header), null);
			}
		} else {
			writer.startElement("fieldset", component);
			writer.writeAttribute("id", component.getClientId(context), null);
			final UIComponent header = component.getFacet("header");
			if ((header != null) && (header.isRendered())) {
				writer.startElement("legend", header);
				writer.writeAttribute("id", header.getClientId(context) + "_header", null);
				header.encodeAll(context);
				writer.endElement("legend");
			}
		}
	}
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		LOGGER.info("children {}", component.getClientId());
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final String injectedClasses = ((Section) component).getInjectedClasses();
		if ((injectedClasses != null) && (!injectedClasses.equals(""))) {
			writer.startElement("div", component);
			writer.writeAttribute("class", injectedClasses, null);
		}
		this.encodeChildrenHelper(context, component, component.getChildren());
		if ((injectedClasses != null) && (!injectedClasses.equals(""))) {
			writer.endElement("div");
		}
	}
	private void encodeChildrenHelper(final FacesContext context, final UIComponent component, final List<UIComponent> children) throws IOException {
		LOGGER.info("helper {}", component.getClientId());
		for (final UIComponent child : children) {
			if (UISort.class.isAssignableFrom(child.getClass())) {
				this.encodeChildrenHelper(context, component, ((UISort) child).sortChildren());
			} else {
				child.encodeAll(context);
			}
		}
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		LOGGER.info("end {}", component.getClientId());
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent rdc = JsfUtility.getInstance().getParentResponseDomain(component);
		if ((rdc != null) && ((UIDropDownResponseDomain.class).isAssignableFrom(rdc.getClass()))) {
			writer.endElement("optgroup");
		} else {
			writer.endElement("fieldset");
		}
	}
}
