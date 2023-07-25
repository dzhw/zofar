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
package de.his.zofar.persistence.question.entities.concrete;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import de.his.zofar.persistence.question.entities.MatrixQuestionComponentEntity;
import de.his.zofar.persistence.question.entities.QuestionEntity;

/**
 * @author le
 * 
 */
@Entity
public class MatrixQuestionEntity extends QuestionEntity {
    @OrderColumn
    @OneToMany(mappedBy = "parentMatrix", cascade = CascadeType.ALL)
    private List<MatrixQuestionComponentEntity> matrixChildren;

    /**
     * 
     */
    public MatrixQuestionEntity() {
        super();
    }

    /**
     * @param questionText
     */
    public MatrixQuestionEntity(String questionText) {
        super(questionText);
    }

    /**
     * @param questionText
     * @param matrixChildren
     */
    public MatrixQuestionEntity(String questionText,
            List<MatrixQuestionComponentEntity> matrixChildren) {
        super(questionText);
        this.matrixChildren = matrixChildren;
    }

    /**
     * @return the matrixChildren
     */
    public List<MatrixQuestionComponentEntity> getMatrixChildren() {
        return matrixChildren;
    }

    /**
     * @param matrixChildren
     *            the matrixChildren to set
     */
    public void setChildren(List<MatrixQuestionComponentEntity> matrixChildren) {
        this.matrixChildren = matrixChildren;
    }

    public void addChildQuestion(MatrixQuestionComponentEntity childQuestion) {
        if (this.matrixChildren == null) {
            this.matrixChildren = new ArrayList<MatrixQuestionComponentEntity>();
        }
        childQuestion.setParentMatrix(this);
        this.matrixChildren.add(childQuestion);
    }

}
