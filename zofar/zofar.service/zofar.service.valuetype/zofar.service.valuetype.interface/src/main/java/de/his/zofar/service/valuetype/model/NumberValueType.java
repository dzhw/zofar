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

import de.his.zofar.service.valuetype.model.possiblevalues.PossibleNumberValue;

public class NumberValueType extends ValueType {

    /**
     *
     */
    private static final long serialVersionUID = -171111488182418679L;

    private Long minimum;

    private Long maximum;

    private Integer decimalPlaces;

    private Map<Long, PossibleNumberValue> possibleValues;

    public void addPossibleValue(final PossibleNumberValue possibleVariableValue) {
        if (this.possibleValues == null) {
            this.possibleValues = new HashMap<Long, PossibleNumberValue>();
        }
        this.possibleValues.put(possibleVariableValue.getValue(),
                possibleVariableValue);
    }

    public Integer getDecimalPlaces() {
        return this.decimalPlaces;
    }

    public Long getMaximum() {
        return this.maximum;
    }

    public Long getMinimum() {
        return this.minimum;
    }

    @Override
    public Map<Long, PossibleNumberValue> getPossibleValues() {
        return this.possibleValues;
    }

    public void setDecimalPlaces(final Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public void setMaximum(final Long maximum) {
        this.maximum = maximum;
    }

    public void setMinimum(final Long minimum) {
        this.minimum = minimum;
    }

    public void setPossibleValues(
            final Map<Long, PossibleNumberValue> possibleValues) {
        this.possibleValues = possibleValues;
    }

}
