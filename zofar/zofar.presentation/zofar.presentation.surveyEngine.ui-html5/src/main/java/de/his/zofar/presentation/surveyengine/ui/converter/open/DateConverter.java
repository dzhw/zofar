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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FacesConverter("org.zofar.open.DateConverter")
public class DateConverter implements Serializable, Converter {

	/**
	 *
	 */
	private static final long serialVersionUID = 3680026520455850356L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DateConverter.class);

	public DateConverter() {
		super();
	}
	
	@Override
	public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
		if (value == null) {
			return null;
		}
		if (component == null) {
			return null;
		}
		if (value.isEmpty()) {
			return null;
		}
		
		Map<String, Object> attributes = component.getAttributes();
		String language = (String) attributes.get("data-lang");
		try {
			final Calendar cal = new GregorianCalendar();
			final SimpleDateFormat stampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			final Date date = new SimpleDateFormat("MMM", Locale.forLanguageTag(language)).parse(value.substring(0,3));
			cal.setTime(date);
			cal.set(Calendar.YEAR, Integer.parseInt(value.substring(4,8)));
			cal.set(Calendar.HOUR, 01);
			return (String)stampFormat.format(cal.getTime());
		} catch (ParseException e) {
		}
		return "";
	}
	@Override
	public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
		if (value == null) {
			return null;
		}
		if (component == null) {
			return null;
		}
		if (value.toString().isEmpty()) {
			return null;
		}
		try {
			Map<String, Object> attributes = component.getAttributes();
			String language = (String) attributes.get("data-lang");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Date parse = sdf.parse(value.toString());
			Calendar c = Calendar.getInstance();
			c.setTime(parse);
			return ""+new SimpleDateFormat("MMM",Locale.forLanguageTag(language)).format(c.getTime())+" "+new SimpleDateFormat("YYYY").format(c.getTime());
		} catch (Exception e) {}
		return "";
	}
}
