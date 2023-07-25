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
package de.his.zofar.presentation.surveyengine.ui.renderer.matrix.singlechoice;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import de.his.zofar.presentation.surveyengine.ui.components.matrix.singlechoice.UISingleChoiceMatrix;
import de.his.zofar.presentation.surveyengine.ui.renderer.ZofarQuestionRenderer;
/**
 * renderer class for SingleChoice Matrix
 *
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UISingleChoiceMatrix.COMPONENT_FAMILY, rendererType = ZofarSingleChoiceMatrixRenderer.RENDERER_TYPE)
public class ZofarSingleChoiceMatrixRenderer extends ZofarQuestionRenderer {
	public static final String RENDERER_TYPE = "org.zofar.SingleChoiceMatrix";
	public ZofarSingleChoiceMatrixRenderer() {
		super();
	}
	@Override
	public boolean getRendersChildren() {
		return true;
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
		super.encodeBegin(context, component);
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
        	child.encodeAll(context);
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
		super.encodeEnd(context, component);
	}
}
