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
package de.his.zofar.persistence.question.entities.questionvalues;

import javax.persistence.Entity;

import de.his.zofar.persistence.question.entities.QuestionVariableEntity;

/**
 * @author le
 * 
 */
@Entity
public class QuestionNumberValueEntity extends QuestionValueEntity {
    private Long value;

    /**
     * 
     */
    public QuestionNumberValueEntity() {
        super();
    }

    /**
     * @param variable
     */
    public QuestionNumberValueEntity(QuestionVariableEntity variable, Long value) {
        super(variable);
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.his.hiob.model.question.entities.questionvalues.QuestionValue#getValue
     * ()
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    @Override
    public void setValue(Object value) {
        if (value != null) {
            if (String.class.isAssignableFrom(value.getClass())) {
                this.value = Long.parseLong((String) value);
            } else if (Long.class.isAssignableFrom(value.getClass())) {
                this.value = (Long) value;
            } else {
                throw new NumberFormatException();
            }
        } else {
            this.value = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        QuestionNumberValueEntity other = (QuestionNumberValueEntity) obj;
        if (value == null) {
            if (other.value != null) return false;
        } else if (!value.equals(other.value)) return false;
        return true;
    }

}
