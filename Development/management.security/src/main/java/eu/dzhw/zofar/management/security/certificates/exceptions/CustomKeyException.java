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
package eu.dzhw.zofar.management.security.certificates.exceptions;
public class CustomKeyException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 8828529374686924368L;
    public CustomKeyException() {
    }
    public CustomKeyException(String message) {
	super(message);
    }
    public CustomKeyException(Throwable cause) {
	super(cause);
    }
    public CustomKeyException(String message, Throwable cause) {
	super(message, cause);
    }
    public CustomKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);
    }
}
