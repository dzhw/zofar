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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.open.options;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.faces.facelets.compiler.UIInstructions;
import de.his.zofar.presentation.surveyengine.ui.components.answer.options.UIAttachedOpenQuestion;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.open.UIOpenMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableItem;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = AbstractTableItem.COMPONENT_FAMILY, rendererType = ZofarOpenMatrixItemRenderer.RENDERER_TYPE)
public class ZofarOpenMatrixItemRenderer extends ZofarMatrixItemRenderer {
	public static final String RENDERER_TYPE = "org.zofar.OpenMatrixItem";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarOpenMatrixItemRenderer.class);
	public ZofarOpenMatrixItemRenderer() {
		super();
	}
	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component)
			throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		final UIComponent header = component.getFacet(ZofarMatrixItemRenderer.HEADER_FACET);
		if (header != null && this.isHasQuestionColumn(context, component)) {
			writer.startElement(ZofarMatrixItemRenderer.TABLE_CELL, component);
			writer.writeAttribute("class",
					"zo-matrix-item-header zo-open-matrix-item-header", null);
			if (header.getChildren().isEmpty()) {
				header.encodeAll(context);
			} else {
				for (final UIComponent child : header.getChildren()) {
					if (UIInstructions.class.isAssignableFrom(child.getClass())) {
						writer.write(JsfUtility.getInstance()
								.evaluateValueExpression(context,
										String.valueOf(child), String.class));
					} else {
						child.encodeAll(context);
					}
				}
			}
			if((UIOpenMatrixItem.class).isAssignableFrom(component.getClass())){
				for (final UIComponent child : component.getChildren()) {
					if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass())){
						child.encodeAll(context);
					}
				}
			}
			writer.endElement(ZofarMatrixItemRenderer.TABLE_CELL);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.component.UIComponentBase#encodeChildren(javax.faces.context
	 * .FacesContext)
	 */
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component)
			throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		for (final UIComponent child : component.getChildren()) {
			if((UIAttachedOpenQuestion.class).isAssignableFrom(child.getClass())){
				continue;
			}
			writer.startElement(ZofarMatrixItemRenderer.TABLE_CELL, component);
			child.encodeAll(context);
			writer.endElement(ZofarMatrixItemRenderer.TABLE_CELL);
		}
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component)
			throws IOException {
		super.encodeEnd(context, component);
	}
}
