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

import de.his.zofar.persistence.question.entities.concrete.MatrixQuestionEntity;

@Entity
public abstract class MatrixQuestionComponentEntity extends QuestionEntity {
    @ManyToOne(cascade = CascadeType.PERSIST)
    private MatrixQuestionEntity parentMatrix;

    /**
     * 
     */
    public MatrixQuestionComponentEntity() {
        super();
    }

    /**
     * @param introduction
     * @param questionText
     * @param instruction
     */
    public MatrixQuestionComponentEntity(String introduction, String questionText,
            String instruction) {
        super(introduction, questionText, instruction);
    }

    /**
     * @param questionText
     */
    public MatrixQuestionComponentEntity(String questionText) {
        super(questionText);
    }

    /**
     * @return the parentMatrix
     */
    public MatrixQuestionEntity getParentMatrix() {
        return parentMatrix;
    }

    /**
     * @param parentMatrix
     *            the parentMatrix to set
     */
    public void setParentMatrix(MatrixQuestionEntity parentMatrix) {
        this.parentMatrix = parentMatrix;
    }
}
