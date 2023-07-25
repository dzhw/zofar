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
package de.his.zofar.presentation.surveyengine.ui.renderer.answers.singechoice.responsedomain;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.ZofarResponseDomainRenderer;
/**
 * @author le
 *
 */
public abstract class ZofarSingleChoiceResponseDomainRenderer extends
        ZofarResponseDomainRenderer {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ZofarSingleChoiceResponseDomainRenderer.class);
	@Override
	public void decode(final FacesContext context, final UIComponent component) {
		final Map<String, String> paramMap = context.getExternalContext()
				.getRequestParameterMap();
		final String clientId = component.getClientId(context);
		if (paramMap.containsKey(clientId)) {
			((UIInput)component).setValue(paramMap.get(clientId));
		} else {
			((UIInput)component).setValue("");
		}
	}
}
