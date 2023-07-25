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

import de.his.zofar.persistence.valuetype.entities.possiblevalues.PossibleStringValueEntity;

@Entity
public class StringValueTypeEntity extends ValueTypeEntity {

    @Column(nullable = false)
    private Integer length;

    @Column(nullable = false)
    private Boolean canBeEmpty;

    @OneToMany(mappedBy = "valueType", fetch = FetchType.LAZY, cascade = ALL)
    @MapKey(name = "value")
    private Map<String, PossibleStringValueEntity> possibleValues;

    public Integer getLength() {
        return this.length;
    }

    public void setLength(final Integer length) {
        this.length = length;
    }

    public Boolean getCanBeEmpty() {
        return this.canBeEmpty;
    }

    public void setCanBeEmpty(final Boolean canBeEmpty) {
        this.canBeEmpty = canBeEmpty;
    }

    public Map<String, PossibleStringValueEntity> getPossibleValues() {
        return this.possibleValues;
    }

    public void setPossibleValues(
            final Map<String, PossibleStringValueEntity> possibleValues) {
        this.possibleValues = possibleValues;
    }

    public void addPossibleValue(final PossibleStringValueEntity possibleVariableValue) {
        if (this.possibleValues == null) {
            this.possibleValues = new HashMap<String, PossibleStringValueEntity>();
        }
        this.possibleValues.put(possibleVariableValue.getValue(),
                possibleVariableValue);
    }

}
