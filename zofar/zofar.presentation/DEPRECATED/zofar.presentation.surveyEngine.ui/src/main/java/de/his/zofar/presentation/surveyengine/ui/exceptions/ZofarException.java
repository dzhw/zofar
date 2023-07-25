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
package de.his.zofar.presentation.surveyengine.ui.exceptions;

/**
 * Marker-Class for Custom Zofar Exceptions
 *
 * @author meisner
 *
 */
public class ZofarException extends RuntimeException {

    private static final long serialVersionUID = 2878261611252219708L;

    public ZofarException() {
        super();
    }

    public ZofarException(final String message) {
        super(message);
    }

    public ZofarException(final Throwable cause) {
        super(cause);
    }

    public ZofarException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ZofarException(final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
