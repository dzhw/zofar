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

import java.util.HashMap;
import java.util.Map;

import de.his.zofar.service.valuetype.model.possiblevalues.PossibleStringValue;

public class StringValueType extends ValueType {

    /**
     *
     */
    private static final long serialVersionUID = -8329508523015454351L;

    private Integer length;

    private Boolean canBeEmpty;

    private Map<String, PossibleStringValue> possibleValues;

    public void addPossibleValue(
            final PossibleStringValue possibleVariableValue) {
        if (this.possibleValues == null) {
            this.possibleValues = new HashMap<String, PossibleStringValue>();
        }
        this.possibleValues.put(possibleVariableValue.getValue(),
                possibleVariableValue);
    }

    public Boolean getCanBeEmpty() {
        return this.canBeEmpty;
    }

    public Integer getLength() {
        return this.length;
    }

    @Override
    public Map<String, PossibleStringValue> getPossibleValues() {
        return this.possibleValues;
    }

    public void setCanBeEmpty(final Boolean canBeEmpty) {
        this.canBeEmpty = canBeEmpty;
    }

    public void setLength(final Integer length) {
        this.length = length;
    }

    public void setPossibleValues(
            final Map<String, PossibleStringValue> possibleValues) {
        this.possibleValues = possibleValues;
    }

}
