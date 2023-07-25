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
package de.his.zofar.presentation.surveyengine.ui.renderer.matrix;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomainUnit;
/**
 * @author meisner
 * 
 */
@FacesRenderer(componentFamily = AbstractTableResponseDomainUnit.COMPONENT_FAMILY, rendererType = ZofarMatrixUnitRenderer.RENDERER_TYPE)
public class ZofarMatrixUnitRenderer extends Renderer {
	
	public static final String RENDERER_TYPE = "org.zofar.MatrixUnit";

	public ZofarMatrixUnitRenderer() {
		super();
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component)
			throws IOException {		
		if (!component.isRendered())
			return;
		
		UIComponent header = component.getFacet("header");
		if((header!=null)&&(header.isRendered()))header.encodeAll(context);
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;
		super.encodeChildren(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		if (!component.isRendered())
			return;
		UIComponent footer = component.getFacet("footer");
		if((footer!=null)&&(footer.isRendered()))footer.encodeAll(context);
	}
	
	

}
