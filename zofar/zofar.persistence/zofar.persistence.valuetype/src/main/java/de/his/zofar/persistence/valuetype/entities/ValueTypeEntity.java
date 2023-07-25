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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import de.his.zofar.persistence.common.entities.BaseEntity;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(initialValue = 1, name = "primary_key_generator",
sequenceName = "SEQ_VALUE_TYPE_ID")
public abstract class ValueTypeEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(unique = false, nullable = true, length = 255)
    private String description;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private MeasurementLevel measurementLevel;

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public MeasurementLevel getMeasurementLevel() {
        return this.measurementLevel;
    }

    public void setMeasurementLevel(
final MeasurementLevel measurementLevel) {
        this.measurementLevel = measurementLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((this.identifier == null) ? 0 : this.identifier.hashCode());
        return result;
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
        final ValueTypeEntity other = (ValueTypeEntity) obj;
        if (this.identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!this.identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }

}
