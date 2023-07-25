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
package de.his.zofar.presentation.surveyengine.ui.renderer.comparison;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonItem;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.comparison.UIComparisonResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;
/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIComparisonItem.COMPONENT_FAMILY, rendererType = ZofarComparisonItemRenderer.RENDERER_TYPE)
public class ZofarComparisonItemRenderer extends Renderer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarComparisonItemRenderer.class);
	public static final String RENDERER_TYPE = "org.zofar.ComparisonItem";
	public ZofarComparisonItemRenderer() {
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
	}
	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}
	}
	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
