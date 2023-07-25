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
package de.his.zofar.presentation.surveyengine.ui.converter.open;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.his.zofar.presentation.surveyengine.ui.renderer.answers.matrix.singlechoice.responsedomain.ZofarSingleChoiceMatrixResponseDomainRenderer;
@FacesConverter("org.zofar.open.NumberConverter")
public class NumberConverter implements Serializable, Converter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3680026520455850356L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NumberConverter.class);
	public NumberConverter() {
		super();
	}
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if(value == null)return null;
		if(component == null)return null;
		final String back = value.replace(',', '.');
		return back;
	}
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if(value == null)return null;
		if(component == null)return null;
		final String back = ((String)value).replace('.',',');
		return back;
	}
}
