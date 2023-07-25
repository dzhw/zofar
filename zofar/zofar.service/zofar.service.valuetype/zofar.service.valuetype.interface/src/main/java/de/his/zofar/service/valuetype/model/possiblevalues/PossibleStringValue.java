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
package de.his.zofar.service.valuetype.model.possiblevalues;
import de.his.zofar.service.valuetype.model.StringValueType;
public class PossibleStringValue extends PossibleValue {
    /**
     *
     */
    private static final long serialVersionUID = -6296872128637014987L;
    private String value;
    private StringValueType valueType;
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PossibleStringValue other = (PossibleStringValue) obj;
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
    public String getValue() {
        return this.value;
    }
    @Override
    public StringValueType getValueType() {
        return this.valueType;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }
    public void setValue(final String value) {
        this.value = value;
    }
    public void setValueType(final StringValueType valueType) {
        this.valueType = valueType;
    }
}
