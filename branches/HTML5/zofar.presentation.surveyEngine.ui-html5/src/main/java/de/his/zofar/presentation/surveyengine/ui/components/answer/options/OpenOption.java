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
package de.his.zofar.presentation.surveyengine.ui.components.answer.options;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.components.common.Visible;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerOption;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.open.options.ZofarTextOpenOptionRenderer;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.OpenOption")
public class OpenOption extends UINamingContainer implements Identificational, Visible,IAnswerOption {
	
	public static final String COMPONENT_FAMILY = "org.zofar.OpenOption";

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenOption.class);

	private Object sequenceId;

	public OpenOption() {
		super();
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	@Override
	public String getRendererType() {
		String rendererType = ZofarTextOpenOptionRenderer.RENDERER_TYPE;
		return rendererType;
	}
	
	@Override
	public void setSequenceId(Object id) {
		this.sequenceId = id;
	}

	@Override
	public Object getSequenceId() {
		return sequenceId;
	}
}
