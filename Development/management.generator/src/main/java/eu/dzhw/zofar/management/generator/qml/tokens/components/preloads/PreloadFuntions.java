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
package eu.dzhw.zofar.management.generator.qml.tokens.components.preloads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreloadFuntions {

	private static final PreloadFuntions INSTANCE = new PreloadFuntions();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PreloadFuntions.class);

	private PreloadFuntions() {
		super();
	}

	public static PreloadFuntions getInstance() {
		return INSTANCE;
	}
	
	public String execute(final Integer index,String function,final String value){
		if(function == null)return value;
		if(function.equals(""))return value;
		else if(function.equals("Fester Wert")){
			return fester_Wert(index,value);
		}
		else if(function.equals("Zufälliger Wert aus")){
			return zufaelliger_Wert_aus(index,value);
		}
		return value;
	}
	
	private String fester_Wert(final Integer index,final String value){
		return value;
	}
	
	private String zufaelliger_Wert_aus(final Integer index,final String value){
		return value;
	}

}
