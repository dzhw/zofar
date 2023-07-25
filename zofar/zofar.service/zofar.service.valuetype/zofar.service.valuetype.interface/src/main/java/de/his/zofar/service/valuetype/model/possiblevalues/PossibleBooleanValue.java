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
package de.his.zofar.service.valuetype.model.possiblevalues;

import de.his.zofar.service.valuetype.model.BooleanValueType;
import de.his.zofar.service.valuetype.model.ValueType;

/**
 * @author le
 *
 */
public class PossibleBooleanValue extends PossibleValue {

    /**
     *
     */
    private static final long serialVersionUID = -1488564230602192947L;

    /**
     *
     */
    private Boolean value;

    /**
     *
     */
    private BooleanValueType valueType;

    /**
     *
     */
    public PossibleBooleanValue() {
        super();
    }

    /**
     * @param value
     *            the value to set
     * @param valueType
     *            the value type to set
     */
    public PossibleBooleanValue(final Boolean value,
            final BooleanValueType valueType) {
        super();
        this.value = value;
        this.valueType = valueType;
    }

    /**
     * @return the value
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final Boolean value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.his.hiob.service.valuetype.external.dtos.possiblevalues.PossibleValueDTO
     * #getValueType()
     */
    @Override
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * @param valueType
     *            the valueType to set
     */
    public void setValueType(final BooleanValueType valueType) {
        this.valueType = valueType;
    }

}
