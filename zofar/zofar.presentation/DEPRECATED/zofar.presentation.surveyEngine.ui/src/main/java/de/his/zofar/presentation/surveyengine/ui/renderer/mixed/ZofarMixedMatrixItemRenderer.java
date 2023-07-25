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
package de.his.zofar.presentation.surveyengine.ui.renderer.mixed;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.faces.facelets.compiler.UIInstructions;
import de.his.zofar.presentation.surveyengine.ui.components.composite.mixed.UIMixedMatrix;
import de.his.zofar.presentation.surveyengine.ui.components.composite.mixed.UIMixedMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = UIMixedMatrixItem.COMPONENT_FAMILY, rendererType = ZofarMixedMatrixItemRenderer.RENDERER_TYPE)
public class ZofarMixedMatrixItemRenderer extends ZofarMatrixItemRenderer {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZofarMixedMatrixItemRenderer.class);
	public static final String RENDERER_TYPE = "rg.zofar.MixedMatrixItem";
	public ZofarMixedMatrixItemRenderer() {
		super();
	}
	@Override
	public synchronized void encodeBegin(FacesContext context,
			UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.startElement("tr", component);
		final UIComponent header = component.getFacet(HEADER_FACET);
		if (header != null && this.isHasQuestionColumn(context, component)) {
			writer.startElement(TABLE_CELL, component);
			writer.writeAttribute("class",
					"zo-matrix-item-header zo-mixed-matrix-item-header", null);
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
			writer.endElement(TABLE_CELL);
		}
	}
	@Override
	protected synchronized Boolean isHasQuestionColumn(
			final FacesContext context, final UIComponent component) {
		Boolean hasQuestionColumn = true;
		UIComponent parent = component;
		while (!((AbstractTableResponseDomain.class).isAssignableFrom(parent
				.getClass())))
			parent = parent.getParent();
		if (parent.getAttributes().get(QUESTION_COLUMN) != null) {
			hasQuestionColumn = Boolean.valueOf(parent.getAttributes().get(
					QUESTION_COLUMN)
					+ "");
		}
		if (hasQuestionColumn) {
			Boolean tmpFlag = false;
			if ((UIMixedMatrixItem.class)
					.isAssignableFrom(component.getClass())) {
				UIMixedMatrixItem tmp = (UIMixedMatrixItem) component;
				final UIComponent header = tmp
						.getFacet(de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer.HEADER_FACET);
				if ((header != null) && (header.getChildren() != null)
						&& (!header.getChildren().isEmpty()))
					tmpFlag = true;
			}
			if (!tmpFlag)
				hasQuestionColumn = false;
		}
		return hasQuestionColumn;
	}
	@Override
	public synchronized void encodeChildren(FacesContext context,
			UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
		int counter=1;
		int counteMMX=100;
		int i=0;
		final ResponseWriter writer = context.getResponseWriter();
		for (final UIComponent child : component.getChildren()) {
			if (UIMixedMatrix.class.isAssignableFrom(child.getClass())){
				i=(counteMMX++);
			}
			else{
				i=(counter++);
			}
			writer.startElement(TABLE_CELL, component);
			writer.writeAttribute("class",
					"zo-matrix-item-header zo-mixed-matrix-item-cell zo-mixed-matrix-item-cell-"+(i)+"", null);
			child.encodeAll(context);
			writer.endElement(TABLE_CELL);
			i++;
		}
	}
	@Override
	public synchronized void encodeEnd(FacesContext context,
			UIComponent component) throws IOException {
		final ResponseWriter writer = context.getResponseWriter();
		writer.endElement("tr");
	}
}
