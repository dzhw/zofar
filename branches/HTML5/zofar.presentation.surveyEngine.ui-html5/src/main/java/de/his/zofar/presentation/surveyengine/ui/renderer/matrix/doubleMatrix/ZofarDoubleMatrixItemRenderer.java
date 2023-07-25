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
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.doublematrix.UIDoubleMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.interfaces.ISequence;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIDoubleMatrixItem.COMPONENT_FAMILY, rendererType = ZofarDoubleMatrixItemRenderer.RENDERER_TYPE)
public class ZofarDoubleMatrixItemRenderer extends ZofarMatrixItemRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DoubleMatrixItem";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarDoubleMatrixItemRenderer.class);
	public ZofarDoubleMatrixItemRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		LOGGER.info("encodeBegin");
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(), null);
		String classes = "row highlight ";
		if (((ISequence.class).isAssignableFrom(component.getClass()))) {
			final Object sequenceId = ((ISequence) component).getSequenceId();
			if (sequenceId != null) {
				final int index = (Integer) sequenceId;
				if (index % 2 == 0)
					classes += "highlight-even ";
				else
					classes += "highlight-odd ";
				classes += "carousel-item";
				if(index == 0)classes += " active";
			}
		}
		writer.writeAttribute("class", classes, null);
	}
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		LOGGER.info("encodeChildren");
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-6 col-md-5", null);
		final UIComponent left = component.getFacet("left");
		if(left != null)left.encodeAll(context);
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-md-2 hidden-sm-down", null);
		final UIComponent question = component.getFacet("question");
		if (question != null) {
			writer.startElement("p", component);
			writer.writeAttribute("class", "text-sub text-center", null);
			writer.write(JsfUtility.getInstance().getTextComponentAsString(context, question));
			writer.endElement("p");
		}
		writer.endElement("div");
		writer.startElement("div", component);
		writer.writeAttribute("class", "col-6 col-md-5", null);
		final UIComponent right = component.getFacet("right");
		if(right != null)right.encodeAll(context);
		writer.endElement("div");
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		LOGGER.info("encodeEnd");
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
