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
package de.his.zofar.presentation.surveyengine.ui.components.answer.responseDomain;
import java.io.IOException;
import java.util.Map;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.components.common.Identificational;
import de.his.zofar.presentation.surveyengine.ui.interfaces.IResponseDomain;
/**
 * renders single choice options as a drop down list. missing options will be
 * rendered as radio input fields by default. the same goes for answer option
 * that contains attached open questions.
 *
 * this component only works properly if JavaScript is enabled.
 *
 * @author le
 *
 */
@Deprecated
@FacesComponent(value = "org.zofar.DropDownSingleChoiceResponseDomain")
public class UIDropDownSingleChoiceResponseDomain extends UIInput implements Identificational,IResponseDomain {
	private static final Logger LOGGER = LoggerFactory.getLogger(UIDropDownSingleChoiceResponseDomain.class);
	private static final String SEPARATED_ITEMS = "separated";
	public UIDropDownSingleChoiceResponseDomain() {
		super();
		this.setRendererType(null);
	}
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	@Override
	public void decode(final FacesContext context) {
		final Map<String, String> requestMap = context.getExternalContext().getRequestParameterMap();
		final String clientId = this.getClientId(context);
		Object submittedValue;
		if (this.isMissingPriority()) {
			submittedValue = requestMap.get(clientId + ":" + SEPARATED_ITEMS);
			if ((submittedValue == null) && (requestMap.get(clientId) != null)) {
				submittedValue = requestMap.get(clientId);
			}
		} else {
			submittedValue = requestMap.get(clientId);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("submitted value: {}", submittedValue);
		}
		this.setSubmittedValue(submittedValue);
	}
	@Override
	public void encodeBegin(final FacesContext context) throws IOException {
	}
	@Override
	public void encodeChildren(final FacesContext context) throws IOException {
	}
	@Override
	public void encodeEnd(final FacesContext context) throws IOException {
	}
	public Boolean isMissingSeparated() {
		final Boolean missingSeparated = (Boolean) this.getAttributes().get("missingSeparated");
		return missingSeparated;
	}
	/**
	 * We do not want to participate in state saving.
	 */
	@Override
	public boolean isTransient() {
		return true;
	}
	/**
	 * if true and JavaScript is disabled, prioritize missing values otherwise
	 * prioritize the values in the drop down list.
	 *
	 * @return whether to prioritize separated missing values and attached open
	 *         questions or the values in the drop down list.
	 */
	public Boolean isMissingPriority() {
		return true;
	}
}
