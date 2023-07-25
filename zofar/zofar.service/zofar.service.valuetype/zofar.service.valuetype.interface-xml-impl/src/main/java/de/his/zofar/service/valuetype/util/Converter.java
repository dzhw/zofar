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
package de.his.zofar.service.valuetype.util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Converter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8629063080358508707L;
	private static final Converter INSTANCE = new Converter();
	public static List<String> MEASUREMENTLEVELS;
	private Converter() {
		super();
		MEASUREMENTLEVELS = new ArrayList<String>();
		MEASUREMENTLEVELS.add("ORDINAL");
		MEASUREMENTLEVELS.add("NOMINAL");
		MEASUREMENTLEVELS.add("INTERVAL");
		MEASUREMENTLEVELS.add("RATIO");
	}
	public static Converter getInstance(){
		return INSTANCE;
	}
	public de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.Enum convertMeasurementLevel(final String level){
		if(level == null)return null;
		if(level.equals("INTERVAL"))return de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.INTERVAL;
		if(level.equals("NOMINAL"))return de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.NOMINAL;
		if(level.equals("ORDINAL"))return de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.ORDINAL;
		if(level.equals("RATIO"))return de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.RATIO;
		return null;
	}
	public String convertMeasurementLevel(final de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.Enum level){
		if(level == null)return null;
		if(level == de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.INTERVAL) return "INTERVAL";
		if(level == de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.NOMINAL) return "NOMINAL";
		if(level == de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.ORDINAL) return "ORDINAL";
		if(level == de.his.zofar.service.valuetype.model.ValueType.MeasurementLevel.RATIO) return "RATIO";
		return null;
	}
}
