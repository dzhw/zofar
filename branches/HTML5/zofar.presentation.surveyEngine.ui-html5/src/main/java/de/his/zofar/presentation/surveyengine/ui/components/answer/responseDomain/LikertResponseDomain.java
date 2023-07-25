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
package de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Alignable;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.likert.responsedomain.ZofarHorizontalLikertResponseDomainRenderer;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.likert.responsedomain.ZofarVerticalLikertResponseDomainRenderer;
/**
 * Renders a Likertscale. Each answer option will be rendered as a radio button.
 *
 * @author Reitmann
 */
@FacesComponent(value = "org.zofar.LikertResponseDomain")
public class LikertResponseDomain extends UIInput implements IResponseDomain, Identificational, Alignable {
	public static final String COMPONENT_FAMILY = "org.zofar.LikertResponseDomain";
	public static final String CSS_CLASS_DELIM = ",";
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(LikertResponseDomain.class);
	public LikertResponseDomain() {
		super();
	}
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	@Override
	public String getRendererType() {
		final Boolean isHorizontal = ((String) this.getAttributes().get("direction")).equals("horizontal");
		if (isHorizontal) {
			return ZofarHorizontalLikertResponseDomainRenderer.RENDERER_TYPE;
		} else {
			return ZofarVerticalLikertResponseDomainRenderer.RENDERER_TYPE;
		}
	}
	@Override
	public String getDirection() {
		return (String) this.getStateHelper().eval("direction");
	}
	@Override
	public void setDirection(final String direction) {
		this.getStateHelper().put("direction", direction);
	}
}
