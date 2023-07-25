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
package de.his.zofar.service.valuetype.model;
import de.his.zofar.service.common.model.BaseDTO;
public abstract class Variable extends BaseDTO {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4532270954579371390L;
	private String uuid;
    private String name;
    private ValueType valueType;
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
        final Variable other = (Variable) obj;
        if (this.uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!this.uuid.equals(other.uuid)) {
            return false;
        }
        return true;
    }
    public String getName() {
        return this.name;
    }
    public String getUuid() {
        return this.uuid;
    }
    public ValueType getValueType() {
        return this.valueType;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        return result;
    }
    public void setName(final String name) {
        this.name = name;
    }
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    public void setValueType(final ValueType valueType) {
        this.valueType = valueType;
    }
}
