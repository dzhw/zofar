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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.open.responsedomain;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;

import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixResponseDomainRenderer;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = AbstractTableResponseDomain.COMPONENT_FAMILY, rendererType = ZofarOpenMatrixResponseDomainRenderer.RENDERER_TYPE)
public class ZofarOpenMatrixResponseDomainRenderer extends
		ZofarMatrixResponseDomainRenderer {

	public static final String RENDERER_TYPE = "org.zofar.OpenMatrixResponseDomain";
	
	public ZofarOpenMatrixResponseDomainRenderer() {
		super();
	}
	
	@Override
	public synchronized void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}

		super.encodeBegin(context, component,"zo-open-matrix-response-domain");
	}

	@Override
	public synchronized void encodeChildren(final FacesContext context,
			final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		super.encodeChildren(context, component);
	}

	@Override
	public synchronized void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered()) {
			return;
		}

		super.encodeEnd(context, component);
	}

}
