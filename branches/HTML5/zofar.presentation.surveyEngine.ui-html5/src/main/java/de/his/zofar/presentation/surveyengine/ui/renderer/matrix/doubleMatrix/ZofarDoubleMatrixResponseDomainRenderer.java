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
package de.his.zofar.presentation.surveyengine.ui.renderer.matrix.doubleMatrix;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.doublematrix.UIDoubleMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIDoubleMatrixResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDoubleMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDoubleMatrixResponseDomainRenderer extends ZofarMatrixResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DoubleMatrixResponseDomain";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarDoubleMatrixResponseDomainRenderer.class);
	public ZofarDoubleMatrixResponseDomainRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), null);
		writer.writeAttribute("class", "form-orientation form-responsive dbl-matrix", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-scroller", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "question-sub", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("p", component);
		writer.writeAttribute("class", "text-head hidden-md-up", null);
		writer.endElement("p");
		writer.startElement("div", component);
		writer.writeAttribute("class", "row", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-6 col-md-5", null);
		final UIComponent leftScaleHeaderFacet = component.getFacet("leftScaleHeader");
		if (leftScaleHeaderFacet != null) {
			writer.startElement("p", component);
			writer.writeAttribute("class", "text-head-sub", null);
			writer.write(JsfUtility.getInstance().getTextComponentAsString(context, leftScaleHeaderFacet));
			writer.endElement("p");
		}
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-6 col-md-5 offset-md-2", null);
		final UIComponent rightScaleHeaderFacet = component.getFacet("rightScaleHeader");
		if (rightScaleHeaderFacet != null) {
			writer.startElement("p", component);
			writer.writeAttribute("class", "text-head-sub", null);
			writer.write(JsfUtility.getInstance().getTextComponentAsString(context, rightScaleHeaderFacet));
			writer.endElement("p");
		}
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		((UIDoubleMatrixResponseDomain) component).sequenceMatrixItems();
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("class", "form-inner form-3-col pt-2 pt-sm-5", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "container", null);
		writer.startElement("p", component);
		writer.writeAttribute("class", "text-head hidden-md-up", null);
		writer.writeAttribute("data-carousel-head-id", component.getClientId(context).replace(':', '_')+"_carousel", null);
		writer.endElement("p");
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context).replace(':', '_')+"_carousel", null);
		writer.writeAttribute("class", "carousel slide", null);
		writer.writeAttribute("data-interval", "false", null);
		writer.startElement("ol", component);
		writer.writeAttribute("class", "dbl-matrix-rdc carousel-indicators carousel-indicators-numbers hidden-md-up", null);
		int lft = 0;
		for (final UIComponent child : component.getChildren()) {
			if ((UIMatrixItem.class).isAssignableFrom(child.getClass())) {
				if (!child.isRendered())
					continue;
				writer.startElement("li", component);
				writer.writeAttribute("data-target",
						"#" + component.getClientId(context).replace(':', '_') + "_carousel", null);
				writer.writeAttribute("data-slide-to", lft, null);
				if (lft == 0)
					writer.writeAttribute("class", "active", null);
				writer.endElement("li");
				lft = lft + 1;
			}
		}
		writer.endElement("ol");
		writer.startElement("div", component);
		writer.writeAttribute("role", "listbox", null);
		writer.writeAttribute("class", "carousel-inner", null);
		for (UIComponent child : component.getChildren()) {
			child.encodeAll(context);
		}
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "carousel-control-outer hidden-md-up", null);
		writer.startElement("a", component);
		writer.writeAttribute("class", "btn btn-secondary carousel-control-prev", null);
		writer.writeAttribute("href", "#"+component.getClientId(context).replace(':', '_')+"_carousel", null);
		writer.writeAttribute("role", "button", null);
		writer.writeAttribute("data-slide", "prev", null);
		writer.startElement("i", component);
		writer.writeAttribute("class", "fa fa-angle-left", null);
		writer.writeAttribute("aria-hidden", "true", null);
		writer.endElement("i");
		writer.startElement("span", component);
		writer.writeAttribute("class", "sr-only", null);
		writer.write("Previous");
		writer.endElement("span");
		writer.endElement("a");
		writer.startElement("a", component);
		writer.writeAttribute("class", "btn btn-secondary carousel-control-next", null);
		writer.writeAttribute("href", "#"+component.getClientId(context).replace(':', '_')+"_carousel", null);
		writer.writeAttribute("role", "button", null);
		writer.writeAttribute("data-slide", "next", null);
		writer.startElement("i", component);
		writer.writeAttribute("class", "fa fa-angle-right", null);
		writer.writeAttribute("aria-hidden", "true", null);
		writer.endElement("i");
		writer.startElement("span", component);
		writer.writeAttribute("class", "sr-only", null);
		writer.write("Next");
		writer.endElement("span");
		writer.endElement("a");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
		writer.endElement("div");
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("div");
	}
}
