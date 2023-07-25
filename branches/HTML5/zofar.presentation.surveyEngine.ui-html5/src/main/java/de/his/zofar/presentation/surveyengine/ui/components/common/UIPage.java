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
package de.his.zofar.presentation.surveyengine.ui.components.common;

import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlForm;

/**
 * @author le
 *
 */
@FacesComponent("org.zofar.Page")
public class UIPage extends HtmlForm {

	private static final String CSS_CLASS = "zo-page";

	private enum PropertyKeys {
		styleClass
	}

	@Override
	public String getRendererType() {
		return "javax.faces.Form";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.html.HtmlForm#getStyleClass()
	 */
	@Override
	public String getStyleClass() {
		final String styleClass = (String) this.getStateHelper().eval(PropertyKeys.styleClass);
		if (styleClass == null) {
			return CSS_CLASS;
		} else {
			return CSS_CLASS + " " + styleClass;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.faces.component.html.HtmlForm#setStyleClass(java.lang.String)
	 */
	@Override
	public void setStyleClass(final String styleClass) {
		this.getStateHelper().put(PropertyKeys.styleClass, styleClass);
	}

}
