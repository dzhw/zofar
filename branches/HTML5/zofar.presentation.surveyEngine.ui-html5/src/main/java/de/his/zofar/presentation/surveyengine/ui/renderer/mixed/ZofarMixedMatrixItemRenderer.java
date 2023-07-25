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
package de.his.zofar.presentation.surveyengine.ui.renderer.mixed;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.faces.facelets.compiler.UIInstructions;

import de.his.zofar.presentation.surveyengine.ui.components.question.composite.mixed.UIMixedMatrix;
import de.his.zofar.presentation.surveyengine.ui.components.question.composite.mixed.UIMixedMatrixItem;
import de.his.zofar.presentation.surveyengine.ui.components.tablebase.AbstractTableResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.ZofarMatrixItemRenderer;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * @author meisner
 *
 */
@FacesRenderer(componentFamily = UIMixedMatrixItem.COMPONENT_FAMILY, rendererType = ZofarMixedMatrixItemRenderer.RENDERER_TYPE)
public class ZofarMixedMatrixItemRenderer extends ZofarMatrixItemRenderer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZofarMixedMatrixItemRenderer.class);

	public static final String RENDERER_TYPE = "rg.zofar.MixedMatrixItem";

	public ZofarMixedMatrixItemRenderer() {
		super();
	}

	@Override
	public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}

		super.encodeBegin(context, component);
	}

	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {

		if (!component.isRendered()) {
			return;
		}

		super.encodeChildren(context, component);
	}

	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		if (!component.isRendered()) {
			return;
		}

		super.encodeEnd(context, component);
	}

}
