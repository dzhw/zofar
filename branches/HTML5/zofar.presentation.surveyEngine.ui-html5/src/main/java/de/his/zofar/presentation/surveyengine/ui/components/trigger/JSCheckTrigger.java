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
package de.his.zofar.presentation.surveyengine.ui.components.trigger;

import java.io.Serializable;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.his.zofar.presentation.surveyengine.ui.interfaces.IAnswerBean;
import de.his.zofar.presentation.surveyengine.ui.util.JsfUtility;

/**
 * @author meisner
 *
 */
@FacesComponent(value = "org.zofar.JSCheckTrigger")
public class JSCheckTrigger extends UINamingContainer implements Serializable {

	private static final long serialVersionUID = -9208937728141596288L;
	private static final Logger LOGGER = LoggerFactory.getLogger(JSCheckTrigger.class);

	public JSCheckTrigger() {
		super();
	}

	public IAnswerBean variable() {
		final IAnswerBean var = (IAnswerBean) this.getAttributes().get("var");
		return var;
	}

	public IAnswerBean xvariable() {
		final IAnswerBean var = (IAnswerBean) this.getAttributes().get("xvar");
		return var;
	}

	public IAnswerBean yvariable() {
		final IAnswerBean var = (IAnswerBean) this.getAttributes().get("yvar");
		return var;
	}

	public boolean isProxy() {
		return false;
	}

	public String getProxyx() {
		return "-1";
	}

	public void setProxyx(final String proxyx) {
		JsfUtility.getInstance().setExpressionValue(this.getFacesContext(), "#{" + this.xvariable().getVariableName() + ".value}", proxyx);
	}

	public String getProxyy() {
		return "-1";
	}

	public void setProxyy(final String proxyy) {
		JsfUtility.getInstance().setExpressionValue(this.getFacesContext(), "#{" + this.yvariable().getVariableName() + ".value}", proxyy);
	}

	public void setProxy(final boolean proxy) {
		JsfUtility.getInstance().setExpressionValue(this.getFacesContext(), "#{" + this.variable().getVariableName() + ".value}", proxy);
	}

}
