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

import javax.faces.component.NamingContainer;

/**
 * interfaces to describe a response domain in Zofar from which all response
 * domains should derived from.
 *
 * all children of a response domain gets client ids which contains the client
 * id of the response domain. @see NamingContainer.
 *
 * @author le
 *
 */
public interface IResponseDomain extends NamingContainer {

	public static final String COMPONENT_FAMILY = "org.zofar.ResponseDomain";
}