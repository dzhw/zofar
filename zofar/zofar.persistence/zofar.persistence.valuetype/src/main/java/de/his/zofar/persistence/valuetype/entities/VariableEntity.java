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

import static javax.persistence.CascadeType.PERSIST;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import de.his.zofar.persistence.common.entities.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(initialValue = 1, name = "primary_key_generator",
        sequenceName = "SEQ_VARIABLE_ID")
public abstract class VariableEntity extends BaseEntity {
    @Column(unique = false, nullable = false)
    private String uuid;

    @Column(unique = false, nullable = false)
    private String name;

    @ManyToOne(cascade = PERSIST)
    private ValueTypeEntity valueType;

    public VariableEntity() {
        super();
    }

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
        final VariableEntity other = (VariableEntity) obj;
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

    public ValueTypeEntity getValueType() {
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

    public void setValueType(final ValueTypeEntity valueType) {
        this.valueType = valueType;
    }
}
