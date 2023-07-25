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

import static javax.persistence.CascadeType.PERSIST;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import de.his.zofar.persistence.common.entities.BaseEntity;
import de.his.zofar.persistence.question.entities.QuestionVariableEntity;

/**
 * @author le
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(initialValue = 1, name = "primary_key_generator",
        sequenceName = "SEQ_QUESTION_VALUE_ID")
public abstract class QuestionValueEntity extends BaseEntity {
    @ManyToOne(optional = false, cascade = PERSIST)
    private QuestionVariableEntity variable;

    /**
     * 
     */
    public QuestionValueEntity() {
        super();
    }

    /**
     * @param variable
     */
    public QuestionValueEntity(QuestionVariableEntity variable) {
        super();
        this.variable = variable;
    }

    /**
     * @return the variable
     */
    public QuestionVariableEntity getVariable() {
        return variable;
    }

    /**
     * @param variable
     *            the variable to set
     */
    public void setVariable(QuestionVariableEntity variable) {
        this.variable = variable;
    }

    public abstract Object getValue();

    public abstract void setValue(Object value);
}
