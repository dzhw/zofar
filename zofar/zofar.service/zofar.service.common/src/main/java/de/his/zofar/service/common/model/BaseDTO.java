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
package de.his.zofar.service.common.model;
import java.io.Serializable;
/**
 * @author Reitmann
 *
 */
public abstract class BaseDTO implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1237540522009651391L;
    private Long id;
    private int version;
    public Long getId() {
        return this.id;
    }
    public int getVersion() {
        return this.version;
    }
    public void setId(final Long id) {
        this.id = id;
    }
    public void setVersion(final int version) {
        this.version = version;
    }
}
