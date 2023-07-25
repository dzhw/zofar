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
package de.his.zofar.persistence.valuetype.entities;

import static javax.persistence.CascadeType.ALL;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import de.his.zofar.persistence.valuetype.entities.possiblevalues.PossibleNumberValueEntity;

@Entity
public class NumberValueTypeEntity extends ValueTypeEntity {

    @Column(nullable = false)
    private Long minimum;

    @Column(nullable = false)
    private Long maximum;

    @Column(nullable = false)
    private Integer decimalPlaces;

    @OneToMany(mappedBy = "valueType", fetch = FetchType.LAZY, cascade = ALL)
    @MapKey(name = "value")
    private Map<Long, PossibleNumberValueEntity> possibleValues;

    public Long getMinimum() {
        return this.minimum;
    }

    public void setMinimum(final Long minimum) {
        this.minimum = minimum;
    }

    public Long getMaximum() {
        return this.maximum;
    }

    public void setMaximum(final Long maximum) {
        this.maximum = maximum;
    }

    public Integer getDecimalPlaces() {
        return this.decimalPlaces;
    }

    public void setDecimalPlaces(final Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Map<Long, PossibleNumberValueEntity> getPossibleValues() {
        return this.possibleValues;
    }

    public void setPossibleValues(
            final Map<Long, PossibleNumberValueEntity> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void addPossibleValue(final PossibleNumberValueEntity possibleVariableValue) {
        if (this.possibleValues == null) {
            this.possibleValues = new HashMap<Long, PossibleNumberValueEntity>();
        }
        this.possibleValues.put(possibleVariableValue.getValue(),
                possibleVariableValue);
    }

}
