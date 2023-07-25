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
package de.his.zofar.presentation.surveyengine.ui.components.question.composite.doublematrix;

import javax.faces.component.FacesComponent;

import de.his.zofar.presentation.surveyengine.ui.components.question.matrix.UIMatrixResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.matrix.doubleMatrix.ZofarDoubleMatrixResponseDomainRenderer;

/**
 * @author le
 *
 */
@FacesComponent(value = "org.zofar.DoubleMatrixResponseDomain")
public class UIDoubleMatrixResponseDomain extends UIMatrixResponseDomain {

	public static final String COMPONENT_FAMILY = "org.zofar.DoubleMatrixResponseDomain";

	public UIDoubleMatrixResponseDomain() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	@Override
	public String getRendererType() {
		return ZofarDoubleMatrixResponseDomainRenderer.RENDERER_TYPE;
	}

}
