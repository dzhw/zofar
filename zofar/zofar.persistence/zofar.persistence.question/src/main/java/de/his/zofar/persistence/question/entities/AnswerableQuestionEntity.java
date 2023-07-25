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
package de.his.zofar.persistence.question.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import de.his.zofar.persistence.valuetype.entities.VariableEntity;

@Entity
public abstract class AnswerableQuestionEntity extends MatrixQuestionComponentEntity {

    @ManyToOne(cascade = CascadeType.PERSIST)
    private VariableEntity variable;

    public AnswerableQuestionEntity() {
        super();
    }

    /**
     * @param questionText
     */
    public AnswerableQuestionEntity(String questionText) {
        super(questionText);
    }

    /**
     * @param questionText
     * @param variable
     */
    public AnswerableQuestionEntity(String questionText, VariableEntity variable) {
        super(questionText);
        this.variable = variable;
    }

    /**
     * @param introduction
     * @param questionText
     * @param instruction
     */
    public AnswerableQuestionEntity(String introduction, String questionText,
            String instruction) {
        super(introduction, questionText, instruction);
    }

    public VariableEntity getVariable() {
        return variable;
    }

    public void setVariable(VariableEntity variable) {
        this.variable = variable;
    }

}
