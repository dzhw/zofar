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
package de.his.zofar.service.valuetype.model;
import java.util.HashMap;
import java.util.Map;
import de.his.zofar.service.valuetype.model.possiblevalues.PossibleBooleanValue;
/**
 * @author le
 *
 */
public class BooleanValueType extends ValueType {
    /**
     *
     */
    private static final long serialVersionUID = -924048173289917976L;
    /**
     *
     */
    private Map<Boolean, PossibleBooleanValue> possibleValues;
    /**
     * @return the possibleValues
     */
    @Override
    public Map<Boolean, PossibleBooleanValue> getPossibleValues() {
        if (possibleValues == null) {
            this.possibleValues =
                    new HashMap<Boolean, PossibleBooleanValue>();
        }
        return possibleValues;
    }
    /**
     * adds a possible value to the value type. if value already exists the
     * value will then be overwritten.
     *
     * @param possibleValue
     */
    public void addPossibleValue(final PossibleBooleanValue possibleValue) {
        if (this.possibleValues == null) {
            this.possibleValues =
                    new HashMap<Boolean, PossibleBooleanValue>();
        }
        if (this.possibleValues.size() > 1) {
            throw new IllegalStateException(
                    "cannot have more than 2 possible values");
        }
        if (possibleValue.getValue() == null) {
            throw new IllegalStateException("possible value must have a value");
        }
        this.possibleValues.put(possibleValue.getValue(), possibleValue);
    }
}
