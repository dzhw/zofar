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
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.singlechoice.UISingleChoiceMatrixItemResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain.ZofarSingleChoiceMatrixItemResponseDomainRenderer;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UISingleChoiceMatrixItemResponseDomain.COMPONENT_FAMILY, rendererType = ZofarDoubleMatrixItemResponseDomainRenderer.RENDERER_TYPE)
public class ZofarDoubleMatrixItemResponseDomainRenderer extends ZofarSingleChoiceMatrixItemResponseDomainRenderer {
	public static final String RENDERER_TYPE = "org.zofar.DoubleMatrixItemResponseDomain";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarDoubleMatrixItemResponseDomainRenderer.class);
	public ZofarDoubleMatrixItemResponseDomainRenderer() {
		super();
	}
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(), null);
		writer.writeAttribute("class", "custom-form custom-form-radio make-2-cols add-pipe pipe-zofar", null);
		writer.startElement("div", component);
		writer.writeAttribute("class", "flex-wrapper", null);
	}
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.endElement("div");
	}
}
