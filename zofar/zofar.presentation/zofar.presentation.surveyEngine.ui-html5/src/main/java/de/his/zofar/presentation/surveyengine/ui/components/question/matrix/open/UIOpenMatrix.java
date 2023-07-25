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
/**
 *
 */
package de.his.zofar.presentation.surveyengine.ui.components.question.matrix.open;
import javax.faces.component.FacesComponent;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrix;
import de.his.zofar.presentation.surveyengine.ui.renderer.matrix.open.ZofarOpenMatrixRenderer;
/**
 * @author le
 *
 */
@FacesComponent("org.zofar.OpenMatrix")
public class UIOpenMatrix extends UIMatrix implements Identificational, Visible {
	public static final String COMPONENT_FAMILY = "org.zofar.OpenMatrix";
	public UIOpenMatrix() {
		super();
	}
	@Override
	public String getRendererType() {
		return ZofarOpenMatrixRenderer.RENDERER_TYPE;
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.UIComponentBase#isTransient()
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
}
