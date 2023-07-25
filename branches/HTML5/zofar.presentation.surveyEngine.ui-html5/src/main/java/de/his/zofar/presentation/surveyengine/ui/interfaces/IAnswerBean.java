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
package de.his.zofar.presentation.surveyengine.ui.interfaces;

/**
 * @author le
 *
 */
public interface IAnswerBean {

	/**
	 * converts the value to to a string as values are stored as strings.
	 * 
	 * @return
	 */
	public abstract String getStringValue();

	/**
	 * @return the variableName
	 */
	public abstract String getVariableName();

	/**
	 * @param stringValue
	 */
	public abstract void setStringValue(String stringValue);

}
